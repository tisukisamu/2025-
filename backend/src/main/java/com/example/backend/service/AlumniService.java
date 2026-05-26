package com.example.backend.service;

import com.example.backend.dto.*;
import com.example.backend.entity.*;
import com.example.backend.exception.BusinessException;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.repository.*;
import com.example.backend.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AlumniService {

    private final AlumniProfileRepository alumniProfileRepository;
    private final AlumniTeamRepository alumniTeamRepository;
    private final AlumniTeamMemberRepository alumniTeamMemberRepository;
    private final AlumniTeamMessageRepository alumniTeamMessageRepository;
    private final StatisticsRepository statisticsRepository;
    private final UserRepository userRepository;

    public AlumniProfileDTO getMyProfile() {
        User user = getCurrentUser();
        AlumniProfile profile = getOrCreateProfile(user);
        return AlumniProfileDTO.fromEntity(profile, null);
    }

    @Transactional
    public AlumniProfileDTO updateMyProfile(UpdateAlumniProfileRequest request) {
        User user = getCurrentUser();
        AlumniProfile profile = getOrCreateProfile(user);
        profile.setSchool(trimOrNull(request.getSchool()));
        profile.setMajor(trimOrNull(request.getMajor()));
        profile.setGraduationYear(request.getGraduationYear());
        profile.setCity(trimOrNull(request.getCity()));
        profile.setLatitude(request.getLatitude());
        profile.setLongitude(request.getLongitude());
        profile.setBio(trimOrNull(request.getBio()));
        if (request.getOpenNearby() != null) {
            profile.setOpenNearby(request.getOpenNearby());
        }
        AlumniProfile saved = alumniProfileRepository.save(profile);
        return AlumniProfileDTO.fromEntity(saved, null);
    }

    public List<AlumniProfileDTO> getNearbyProfiles(double radiusKm) {
        User user = getCurrentUser();
        AlumniProfile mine = getOrCreateProfile(user);
        if (mine.getLatitude() == null || mine.getLongitude() == null) {
            throw new BusinessException(400, "请先在校友资料中设置经纬度");
        }
        return alumniProfileRepository.findByOpenNearbyTrue().stream()
                .filter(p -> !p.getUser().getId().equals(user.getId()))
                .map(p -> {
                    Double distance = calcDistanceKm(mine.getLatitude(), mine.getLongitude(), p.getLatitude(), p.getLongitude());
                    return distance == null ? null : AlumniProfileDTO.fromEntity(p, distance);
                })
                .filter(dto -> dto != null && dto.getDistanceKm() != null && dto.getDistanceKm() <= radiusKm)
                .sorted(Comparator.comparing(AlumniProfileDTO::getDistanceKm))
                .collect(Collectors.toList());
    }

    @Transactional
    public AlumniTeamDTO createTeam(CreateAlumniTeamRequest request) {
        User user = getCurrentUser();
        AlumniProfile profile = getOrCreateProfile(user);
        if (profile.getLatitude() == null || profile.getLongitude() == null) {
            throw new BusinessException(400, "请先设置经纬度再创建小队");
        }

        AlumniTeam team = new AlumniTeam();
        team.setName(request.getName().trim());
        team.setSlogan(trimOrNull(request.getSlogan()));
        team.setCity(trimOrNull(request.getCity()) != null ? trimOrNull(request.getCity()) : profile.getCity());
        team.setLatitude(profile.getLatitude());
        team.setLongitude(profile.getLongitude());
        team.setMaxMembers((request.getMaxMembers() == null || request.getMaxMembers() < 2) ? 6 : Math.min(request.getMaxMembers(), 50));
        team.setOwner(user);

        AlumniTeam savedTeam = alumniTeamRepository.save(team);

        AlumniTeamMember ownerMember = new AlumniTeamMember();
        ownerMember.setTeam(savedTeam);
        ownerMember.setUser(user);
        ownerMember.setRole(AlumniTeamMember.Role.OWNER);
        alumniTeamMemberRepository.save(ownerMember);

        return AlumniTeamDTO.fromEntity(savedTeam, 1, 0.0, true);
    }

    public List<AlumniTeamDTO> getNearbyTeams(double radiusKm) {
        User user = getCurrentUser();
        AlumniProfile mine = getOrCreateProfile(user);
        if (mine.getLatitude() == null || mine.getLongitude() == null) {
            throw new BusinessException(400, "请先在校友资料中设置经纬度");
        }
        return alumniTeamRepository.findByStatusOrderByCreatedAtDesc(AlumniTeam.Status.ACTIVE).stream()
                .map(team -> {
                    Double distance = calcDistanceKm(mine.getLatitude(), mine.getLongitude(), team.getLatitude(), team.getLongitude());
                    if (distance == null || distance > radiusKm) {
                        return null;
                    }
                    int count = (int) alumniTeamMemberRepository.countByTeamId(team.getId());
                    boolean joined = alumniTeamMemberRepository.existsByTeamIdAndUserId(team.getId(), user.getId());
                    return AlumniTeamDTO.fromEntity(team, count, distance, joined);
                })
                .filter(dto -> dto != null)
                .sorted(Comparator.comparing(AlumniTeamDTO::getDistanceKm))
                .collect(Collectors.toList());
    }

    public List<AlumniTeamDTO> getMyTeams() {
        User user = getCurrentUser();
        return alumniTeamMemberRepository.findByUserIdOrderByJoinedAtDesc(user.getId()).stream()
                .map(member -> {
                    AlumniTeam team = member.getTeam();
                    int count = (int) alumniTeamMemberRepository.countByTeamId(team.getId());
                    return AlumniTeamDTO.fromEntity(team, count, null, true);
                })
                .collect(Collectors.toList());
    }

    public List<AlumniRankingDTO> getNearbyRanking(double radiusKm, int limit) {
        User user = getCurrentUser();
        AlumniProfile mine = getOrCreateProfile(user);
        if (mine.getLatitude() == null || mine.getLongitude() == null) {
            return List.of();
        }
        List<AlumniRankingDTO> rows = new ArrayList<>();
        for (AlumniProfile profile : alumniProfileRepository.findByOpenNearbyTrue()) {
            if (profile.getUser().getId().equals(user.getId())) {
                continue;
            }
            Double distance = calcDistanceKm(mine.getLatitude(), mine.getLongitude(), profile.getLatitude(), profile.getLongitude());
            if (distance == null || distance > radiusKm) {
                continue;
            }
            Long totalChecks = statisticsRepository.sumTotalDaysByUserId(profile.getUser().getId());
            Double avgRate = statisticsRepository.avgCompleteRateByUserId(profile.getUser().getId());
            long checks = totalChecks == null ? 0L : totalChecks;
            double rate = avgRate == null ? 0.0 : Math.round(avgRate * 100.0) / 100.0;
            double score = Math.round((checks + rate * 2) * 100.0) / 100.0;
            rows.add(AlumniRankingDTO.builder()
                    .userId(profile.getUser().getId())
                    .userName(profile.getUser().getName())
                    .userAvatar(profile.getUser().getAvatar())
                    .school(profile.getSchool())
                    .city(profile.getCity())
                    .distanceKm(distance)
                    .totalChecks(checks)
                    .avgRate(rate)
                    .score(score)
                    .build());
        }
        rows.sort(Comparator
                .comparing(AlumniRankingDTO::getTotalChecks).reversed()
                .thenComparing(AlumniRankingDTO::getAvgRate).reversed()
                .thenComparing(AlumniRankingDTO::getDistanceKm));
        AtomicInteger index = new AtomicInteger(1);
        return rows.stream()
                .limit(Math.max(limit, 1))
                .peek(item -> item.setRank(index.getAndIncrement()))
                .collect(Collectors.toList());
    }

    public AlumniTeamDetailDTO getTeamDetail(Long teamId) {
        User user = getCurrentUser();
        AlumniTeam team = alumniTeamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("校友小队", "id", teamId));
        if (!alumniTeamMemberRepository.existsByTeamIdAndUserId(teamId, user.getId())) {
            throw new BusinessException(403, "加入小队后才可查看详情");
        }

        int count = (int) alumniTeamMemberRepository.countByTeamId(teamId);
        AlumniTeamDTO teamDTO = AlumniTeamDTO.fromEntity(team, count, null, true);
        List<AlumniTeamMemberDTO> members = alumniTeamMemberRepository.findByTeamIdOrderByJoinedAtAsc(teamId).stream()
                .map(this::toMemberDTO)
                .sorted(Comparator.comparing(AlumniTeamMemberDTO::getTotalChecks).reversed())
                .collect(Collectors.toList());
        List<AlumniTeamMessageDTO> messages = alumniTeamMessageRepository.findByTeamIdOrderByCreatedAtDesc(teamId).stream()
                .limit(50)
                .map(AlumniTeamMessageDTO::fromEntity)
                .collect(Collectors.toList());

        return AlumniTeamDetailDTO.builder()
                .team(teamDTO)
                .members(members)
                .messages(messages)
                .build();
    }

    @Transactional
    public AlumniTeamDTO joinTeam(Long teamId) {
        User user = getCurrentUser();
        AlumniTeam team = alumniTeamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("校友小队", "id", teamId));
        if (team.getStatus() != AlumniTeam.Status.ACTIVE) {
            throw new BusinessException(400, "该小队已不可加入");
        }
        if (alumniTeamMemberRepository.existsByTeamIdAndUserId(teamId, user.getId())) {
            throw new BusinessException(400, "你已在该小队中");
        }
        long count = alumniTeamMemberRepository.countByTeamId(teamId);
        if (count >= team.getMaxMembers()) {
            throw new BusinessException(400, "小队人数已满");
        }

        AlumniTeamMember member = new AlumniTeamMember();
        member.setTeam(team);
        member.setUser(user);
        member.setRole(AlumniTeamMember.Role.MEMBER);
        alumniTeamMemberRepository.save(member);

        return AlumniTeamDTO.fromEntity(team, (int) (count + 1), null, true);
    }

    @Transactional
    public void quitTeam(Long teamId) {
        User user = getCurrentUser();
        AlumniTeamMember member = alumniTeamMemberRepository.findByTeamIdAndUserId(teamId, user.getId())
                .orElseThrow(() -> new BusinessException(400, "你不在该小队中"));
        AlumniTeam team = member.getTeam();
        long count = alumniTeamMemberRepository.countByTeamId(teamId);

        if (member.getRole() == AlumniTeamMember.Role.OWNER && count > 1) {
            throw new BusinessException(400, "队长需先解散小队或转移队长后再退出");
        }
        alumniTeamMemberRepository.delete(member);
        if (member.getRole() == AlumniTeamMember.Role.OWNER) {
            team.setStatus(AlumniTeam.Status.DISSOLVED);
            alumniTeamRepository.save(team);
        }
    }

    @Transactional
    public AlumniTeamMessageDTO createTeamMessage(Long teamId, CreateAlumniTeamMessageRequest request) {
        User user = getCurrentUser();
        AlumniTeam team = alumniTeamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("校友小队", "id", teamId));
        if (!alumniTeamMemberRepository.existsByTeamIdAndUserId(teamId, user.getId())) {
            throw new BusinessException(403, "加入小队后才可发送督促消息");
        }
        AlumniTeamMessage message = new AlumniTeamMessage();
        message.setTeam(team);
        message.setUser(user);
        message.setContent(request.getContent().trim());
        AlumniTeamMessage saved = alumniTeamMessageRepository.save(message);
        return AlumniTeamMessageDTO.fromEntity(saved);
    }

    public List<AlumniTeamMessageDTO> getTeamMessages(Long teamId) {
        User user = getCurrentUser();
        if (!alumniTeamMemberRepository.existsByTeamIdAndUserId(teamId, user.getId())) {
            throw new BusinessException(403, "加入小队后才可查看督促消息");
        }
        return alumniTeamMessageRepository.findByTeamIdOrderByCreatedAtDesc(teamId).stream()
                .limit(50)
                .map(AlumniTeamMessageDTO::fromEntity)
                .collect(Collectors.toList());
    }

    private AlumniTeamMemberDTO toMemberDTO(AlumniTeamMember member) {
        Long totalChecks = statisticsRepository.sumTotalDaysByUserId(member.getUser().getId());
        Double avgRate = statisticsRepository.avgCompleteRateByUserId(member.getUser().getId());
        return AlumniTeamMemberDTO.fromEntity(
                member,
                totalChecks == null ? 0L : totalChecks,
                avgRate == null ? 0.0 : Math.round(avgRate * 100.0) / 100.0
        );
    }

    private AlumniProfile getOrCreateProfile(User user) {
        return alumniProfileRepository.findByUserId(user.getId()).orElseGet(() -> {
            AlumniProfile profile = new AlumniProfile();
            profile.setUser(user);
            profile.setOpenNearby(true);
            return alumniProfileRepository.save(profile);
        });
    }

    private User getCurrentUser() {
        org.springframework.security.core.Authentication auth =
                org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
        return userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new ResourceNotFoundException("用户", "id", userDetails.getId()));
    }

    private String trimOrNull(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return value.trim();
    }

    private Double calcDistanceKm(Double lat1, Double lng1, Double lat2, Double lng2) {
        if (lat1 == null || lng1 == null || lat2 == null || lng2 == null) {
            return null;
        }
        double earthRadiusKm = 6371.0;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return Math.round(earthRadiusKm * c * 100.0) / 100.0;
    }
}
