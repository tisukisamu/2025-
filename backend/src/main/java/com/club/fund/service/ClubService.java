package com.club.fund.service;

import com.club.fund.common.Constants;
import com.club.fund.dto.request.ClubCreateRequest;
import com.club.fund.dto.response.ClubResponse;
import com.club.fund.dto.response.UserResponse;
import com.club.fund.entity.Club;
import com.club.fund.entity.ClubMember;
import com.club.fund.entity.FundApply;
import com.club.fund.entity.FundFlow;
import com.club.fund.entity.User;
import com.club.fund.exception.BusinessException;
import com.club.fund.repository.ClubMemberRepository;
import com.club.fund.repository.ClubRepository;
import com.club.fund.repository.FundApplyRepository;
import com.club.fund.repository.FundFlowRepository;
import com.club.fund.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClubService {

    private final ClubRepository clubRepository;
    private final ClubMemberRepository clubMemberRepository;
    private final UserRepository userRepository;
    private final FundFlowRepository fundFlowRepository;
    private final FundApplyRepository fundApplyRepository;

    @Transactional
    public ClubResponse createClub(ClubCreateRequest request) {
        if (clubRepository.existsByClubCode(request.getClubCode())) {
            throw new BusinessException("社团编码已存在");
        }

        Club club = new Club();
        club.setClubName(request.getClubName());
        club.setClubCode(request.getClubCode());
        club.setDescription(request.getDescription());
        club.setLogo(request.getLogo());
        club.setCategory(request.getCategory());
        club.setStatus(1);
        club.setDeleted(0);

        if (request.getPresidentId() != null) {
            User president = userRepository.findById(request.getPresidentId())
                    .orElseThrow(() -> new BusinessException("社长用户不存在"));
            club.setPresident(president);
        }

        if (request.getTeacherId() != null) {
            User teacher = userRepository.findById(request.getTeacherId())
                    .orElseThrow(() -> new BusinessException("指导老师不存在"));
            club.setTeacher(teacher);
        }

        club = clubRepository.save(club);
        return convertToResponse(club);
    }

    @Transactional
    public ClubResponse updateClub(Long id, ClubCreateRequest request) {
        Club club = clubRepository.findById(id)
                .orElseThrow(() -> new BusinessException("社团不存在"));

        club.setClubName(request.getClubName());
        club.setDescription(request.getDescription());
        club.setLogo(request.getLogo());
        club.setCategory(request.getCategory());

        if (request.getPresidentId() != null) {
            User president = userRepository.findById(request.getPresidentId())
                    .orElseThrow(() -> new BusinessException("社长用户不存在"));
            club.setPresident(president);
        }

        if (request.getTeacherId() != null) {
            User teacher = userRepository.findById(request.getTeacherId())
                    .orElseThrow(() -> new BusinessException("指导老师不存在"));
            club.setTeacher(teacher);
        }

        club = clubRepository.save(club);
        return convertToResponse(club);
    }

    public ClubResponse getClubById(Long id) {
        Club club = clubRepository.findById(id)
                .orElseThrow(() -> new BusinessException("社团不存在"));
        return convertToResponse(club);
    }

    public Page<ClubResponse> getClubList(Long userId, String role, Pageable pageable) {
        if (Constants.ROLE_ADMIN.equals(role)) {
            return clubRepository.findAllActive(pageable).map(this::convertToResponse);
        } else if ("president".equals(role)) {
            List<Club> clubs = clubRepository.findByPresidentId(userId);
            List<ClubResponse> responses = clubs.stream().map(this::convertToResponse).collect(Collectors.toList());
            return new org.springframework.data.domain.PageImpl<>(responses, pageable, responses.size());
        } else if ("teacher".equals(role)) {
            List<Club> clubs = clubRepository.findByTeacherId(userId);
            List<ClubResponse> responses = clubs.stream().map(this::convertToResponse).collect(Collectors.toList());
            return new org.springframework.data.domain.PageImpl<>(responses, pageable, responses.size());
        } else if ("member".equals(role)) {
            List<ClubMember> members = clubMemberRepository.findActiveClubsByUserId(userId);
            List<Club> clubs = members.stream()
                    .map(ClubMember::getClub)
                    .collect(Collectors.toList());
            List<ClubResponse> responses = clubs.stream().map(this::convertToResponse).collect(Collectors.toList());
            return new org.springframework.data.domain.PageImpl<>(responses, pageable, responses.size());
        }
        return new org.springframework.data.domain.PageImpl<>(List.of(), pageable, 0);
    }

    public Page<ClubResponse> searchClubs(String keyword, Pageable pageable) {
        return clubRepository.searchByKeyword(keyword, pageable).map(this::convertToResponse);
    }

    public List<ClubResponse> getClubsByPresidentId(Long presidentId) {
        return clubRepository.findByPresidentId(presidentId).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public List<ClubResponse> getClubsByTeacherId(Long teacherId) {
        return clubRepository.findByTeacherId(teacherId).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public List<ClubResponse> getAllActiveClubs() {
        return clubRepository.findAllActiveList().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public List<ClubResponse> getClubsByMemberId(Long memberId) {
        return clubMemberRepository.findActiveClubsByUserId(memberId).stream()
                .map(member -> convertToResponse(member.getClub()))
                .collect(Collectors.toList());
    }

    @Transactional
    public void addMember(Long clubId, Long userId, String position) {
        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new BusinessException("社团不存在"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("用户不存在"));

        if (clubMemberRepository.existsByClubIdAndUserId(clubId, userId)) {
            throw new BusinessException("用户已在社团中");
        }

        ClubMember member = new ClubMember();
        member.setClub(club);
        member.setUser(user);
        member.setPosition(position);
        member.setStatus(1);
        clubMemberRepository.save(member);
    }

    @Transactional
    public void removeMember(Long clubId, Long userId) {
        int result = clubMemberRepository.removeMember(clubId, userId);
        if (result == 0) {
            throw new BusinessException("成员不存在");
        }
    }

    @Transactional
    public void updateMemberPosition(Long clubId, Long userId, String position) {
        ClubMember member = clubMemberRepository.findByClubIdAndUserId(clubId, userId)
                .orElseThrow(() -> new BusinessException("成员不存在"));
        member.setPosition(position);
        clubMemberRepository.save(member);
    }

    public Page<UserResponse> getClubMembers(Long clubId, Pageable pageable) {
        return clubMemberRepository.findByClubId(clubId, pageable)
                .map(member -> {
                    UserResponse response = new UserResponse();
                    response.setId(member.getUser().getId());
                    response.setUsername(member.getUser().getUsername());
                    response.setRealName(member.getUser().getRealName());
                    response.setAvatar(member.getUser().getAvatar());
                    response.setStudentId(member.getUser().getStudentId());
                    response.setPosition(member.getPosition());
                    response.setStatus(member.getStatus());
                    response.setJoinTime(member.getJoinTime());
                    return response;
                });
    }

    @Transactional
    public void deleteClub(Long id) {
        Club club = clubRepository.findById(id)
                .orElseThrow(() -> new BusinessException("社团不存在"));
        club.setDeleted(1);
        clubRepository.save(club);
    }

    @Transactional
    public ClubResponse updateClubStatus(Long id, Integer status) {
        Club club = clubRepository.findById(id)
                .orElseThrow(() -> new BusinessException("社团不存在"));
        club.setStatus(status != null && status == 1 ? 1 : 0);
        club = clubRepository.save(club);
        return convertToResponse(club);
    }

    private ClubResponse convertToResponse(Club club) {
        ClubResponse response = new ClubResponse();
        response.setId(club.getId());
        response.setClubName(club.getClubName());
        response.setClubCode(club.getClubCode());
        response.setDescription(club.getDescription());
        response.setLogo(club.getLogo());
        response.setCategory(club.getCategory());
        response.setBalance(club.getBalance());
        response.setStatus(club.getStatus());
        response.setCreateTime(club.getCreateTime());

        if (club.getPresident() != null) {
            UserResponse president = new UserResponse();
            president.setId(club.getPresident().getId());
            president.setRealName(club.getPresident().getRealName());
            response.setPresident(president);
        }

        if (club.getTeacher() != null) {
            UserResponse teacher = new UserResponse();
            teacher.setId(club.getTeacher().getId());
            teacher.setRealName(club.getTeacher().getRealName());
            response.setTeacher(teacher);
        }

        response.setMemberCount(clubMemberRepository.countByClubId(club.getId()));

        YearMonth currentMonth = YearMonth.now();
        LocalDateTime monthStart = currentMonth.atDay(1).atStartOfDay();
        LocalDateTime monthEnd = currentMonth.atEndOfMonth().atTime(23, 59, 59);

        List<FundFlow> monthFlows = fundFlowRepository.findByClubIdAndTimeRange(
                club.getId(), monthStart, monthEnd, Pageable.unpaged()).getContent();

        BigDecimal monthIncome = monthFlows.stream()
                .filter(flow -> "INCOME".equals(flow.getFlowType()))
                .map(FundFlow::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal monthExpense = monthFlows.stream()
                .filter(flow -> "EXPENSE".equals(flow.getFlowType()))
                .map(FundFlow::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        response.setMonthIncome(monthIncome);
        response.setMonthExpense(monthExpense);

        List<FundApply> pendingApplies = fundApplyRepository.findByClubIdAndStatusIn(
                club.getId(), List.of("PENDING", "PRESIDENT_APPROVED"));
        response.setPendingApprovalCount(pendingApplies.size());

        return response;
    }
}
