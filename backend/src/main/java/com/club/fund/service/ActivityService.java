package com.club.fund.service;

import com.club.fund.dto.request.ActivityCreateRequest;
import com.club.fund.dto.response.ActivityResponse;
import com.club.fund.dto.response.ClubResponse;
import com.club.fund.entity.Activity;
import com.club.fund.entity.ActivitySignup;
import com.club.fund.entity.Club;
import com.club.fund.entity.User;
import com.club.fund.exception.BusinessException;
import com.club.fund.repository.ActivityRepository;
import com.club.fund.repository.ActivitySignupRepository;
import com.club.fund.repository.ClubRepository;
import com.club.fund.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ActivityService {

    private final ActivityRepository activityRepository;
    private final ActivitySignupRepository activitySignupRepository;
    private final ClubRepository clubRepository;
    private final UserRepository userRepository;

    @Transactional
    public ActivityResponse createActivity(ActivityCreateRequest request) {
        Club club = clubRepository.findById(request.getClubId())
                .orElseThrow(() -> new BusinessException("社团不存在"));

        Activity activity = new Activity();
        activity.setClub(club);
        activity.setActivityName(request.getActivityName());
        activity.setDescription(request.getDescription());
        activity.setStartTime(request.getStartTime());
        activity.setEndTime(request.getEndTime());
        activity.setLocation(request.getLocation());
        activity.setBudget(request.getBudget());
        activity.setCoverImage(request.getCoverImage());
        activity.setStatus("DRAFT");
        activity.setDeleted(0);

        activity = activityRepository.save(activity);
        return convertToResponse(activity);
    }

    @Transactional
    public ActivityResponse updateActivity(Long id, ActivityCreateRequest request) {
        Activity activity = activityRepository.findById(id)
                .orElseThrow(() -> new BusinessException("活动不存在"));

        activity.setActivityName(request.getActivityName());
        activity.setDescription(request.getDescription());
        activity.setStartTime(request.getStartTime());
        activity.setEndTime(request.getEndTime());
        activity.setLocation(request.getLocation());
        activity.setBudget(request.getBudget());
        activity.setCoverImage(request.getCoverImage());

        activity = activityRepository.save(activity);
        return convertToResponse(activity);
    }

    @Transactional
    public ActivityResponse publishActivity(Long id) {
        Activity activity = activityRepository.findById(id)
                .orElseThrow(() -> new BusinessException("活动不存在"));

        activity.setStatus("PUBLISHED");
        activity = activityRepository.save(activity);
        return convertToResponse(activity);
    }

    @Transactional
    public ActivityResponse submitForReview(Long id) {
        Activity activity = activityRepository.findById(id)
                .orElseThrow(() -> new BusinessException("活动不存在"));

        activity.setStatus("SUBMITTED");
        activity = activityRepository.save(activity);
        return convertToResponse(activity);
    }

    @Transactional
    public ActivityResponse approveActivity(Long id) {
        Activity activity = activityRepository.findById(id)
                .orElseThrow(() -> new BusinessException("活动不存在"));
        activity.setStatus("PUBLISHED");
        activity = activityRepository.save(activity);
        return convertToResponse(activity);
    }

    @Transactional
    public ActivityResponse rejectActivity(Long id) {
        Activity activity = activityRepository.findById(id)
                .orElseThrow(() -> new BusinessException("活动不存在"));
        activity.setStatus("REJECTED");
        activity = activityRepository.save(activity);
        return convertToResponse(activity);
    }

    public ActivityResponse getActivityById(Long id) {
        Activity activity = activityRepository.findById(id)
                .orElseThrow(() -> new BusinessException("活动不存在"));
        return convertToResponse(activity);
    }

    public Page<ActivityResponse> getActivityList(Long clubId, Pageable pageable) {
        if (clubId != null) {
            return activityRepository.findByClubId(clubId, pageable).map(this::convertToResponse);
        }
        return activityRepository.findAllPublished(pageable).map(this::convertToResponse);
    }

    public Page<ActivityResponse> searchActivities(String keyword, Pageable pageable) {
        return activityRepository.searchByKeyword(keyword, pageable).map(this::convertToResponse);
    }

    public Page<ActivityResponse> getActivityReviewList(Long reviewerId, String roleCode, Pageable pageable) {
        if ("admin".equals(roleCode)) {
            return activityRepository.findByStatus("SUBMITTED", pageable).map(this::convertToResponse);
        }
        if (!"teacher".equals(roleCode)) {
            return Page.empty(pageable);
        }
        List<Club> clubs = clubRepository.findByTeacherId(reviewerId);
        List<Long> clubIds = clubs.stream().map(Club::getId).collect(Collectors.toList());
        if (clubIds.isEmpty()) {
            return Page.empty(pageable);
        }
        return activityRepository.findByClubIdsAndStatus(clubIds, "SUBMITTED", pageable).map(this::convertToResponse);
    }

    public Page<ActivityResponse> getMyCreatedActivities(Long userId, String roleCode, Pageable pageable) {
        if ("admin".equals(roleCode)) {
            return activityRepository.findAllNonDeleted(pageable).map(this::convertToResponse);
        }
        if (!"president".equals(roleCode)) {
            return Page.empty(pageable);
        }
        List<Club> clubs = clubRepository.findByPresidentId(userId);
        List<Long> clubIds = clubs.stream().map(Club::getId).collect(Collectors.toList());
        if (clubIds.isEmpty()) {
            return Page.empty(pageable);
        }
        return activityRepository.findByClubIds(clubIds, pageable).map(this::convertToResponse);
    }

    public List<ActivityResponse> getMySignupActivities(Long userId) {
        return activitySignupRepository.findByUserId(userId).stream()
                .filter(signup -> signup.getStatus() != null && signup.getStatus() == 1)
                .map(ActivitySignup::getActivity)
                .filter(activity -> activity.getDeleted() != null && activity.getDeleted() == 0)
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void signupActivity(Long activityId, Long userId) {
        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new BusinessException("活动不存在"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("用户不存在"));

        if (activitySignupRepository.existsActiveSignup(activityId, userId)) {
            throw new BusinessException("已报名该活动");
        }

        ActivitySignup signup = activitySignupRepository.findByActivityIdAndUserId(activityId, userId)
                .orElse(null);
        
        if (signup != null) {
            signup.setStatus(1);
            activitySignupRepository.save(signup);
        } else {
            signup = new ActivitySignup();
            signup.setActivity(activity);
            signup.setUser(user);
            signup.setStatus(1);
            activitySignupRepository.save(signup);
        }
    }

    @Transactional
    public void cancelSignup(Long activityId, Long userId) {
        int result = activitySignupRepository.cancelSignup(activityId, userId);
        if (result == 0) {
            throw new BusinessException("未报名该活动");
        }
    }

    @Transactional
    public void deleteActivity(Long id) {
        Activity activity = activityRepository.findById(id)
                .orElseThrow(() -> new BusinessException("活动不存在"));
        activity.setDeleted(1);
        activityRepository.save(activity);
    }

    private ActivityResponse convertToResponse(Activity activity) {
        ActivityResponse response = new ActivityResponse();
        response.setId(activity.getId());
        response.setActivityName(activity.getActivityName());
        response.setDescription(activity.getDescription());
        response.setStartTime(activity.getStartTime());
        response.setEndTime(activity.getEndTime());
        response.setLocation(activity.getLocation());
        response.setBudget(activity.getBudget());
        response.setStatus(activity.getStatus());
        response.setCoverImage(activity.getCoverImage());
        response.setCreateTime(activity.getCreateTime());

        if (activity.getClub() != null) {
            ClubResponse clubResponse = new ClubResponse();
            clubResponse.setId(activity.getClub().getId());
            clubResponse.setClubName(activity.getClub().getClubName());
            response.setClub(clubResponse);
        }

        response.setSignupCount(activitySignupRepository.countByActivityId(activity.getId()));

        return response;
    }
}
