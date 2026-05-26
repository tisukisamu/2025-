package com.example.backend.service;

import com.example.backend.dto.ApplicationDTO;
import com.example.backend.entity.Application;
import com.example.backend.entity.Job;
import com.example.backend.exception.BusinessException;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.repository.ApplicationRepository;
import com.example.backend.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final JobRepository jobRepository;

    public List<Application> getApplicationsByUserId(Long userId) {
        return applicationRepository.findByUserId(userId);
    }

    public Application getApplicationById(Long id) {
        return applicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("投递记录不存在"));
    }

    public List<Application> getApplicationsByCompanyId(Long companyId) {
        List<Job> jobs = jobRepository.findByCompanyId(companyId);
        List<Long> jobIds = jobs.stream().map(Job::getId).toList();
        return applicationRepository.findByJobIdIn(jobIds);
    }

    @Transactional
    public Application createApplication(ApplicationDTO applicationDTO) {
        if (applicationDTO.getUserId() == null) {
            throw new BusinessException("用户未登录或身份无效");
        }
        if (applicationRepository.existsByUserIdAndJobId(applicationDTO.getUserId(), applicationDTO.getJobId())) {
            throw new BusinessException("您已投递过该职位");
        }

        Application application = new Application();
        application.setUserId(applicationDTO.getUserId());
        application.setJobId(applicationDTO.getJobId());
        application.setResumeId(applicationDTO.getResumeId());
        application.setStatus(Application.Status.PENDING);
        return applicationRepository.save(application);
    }

    @Transactional
    public void updateApplicationStatus(Long id, Application.Status status) {
        Application application = getApplicationById(id);
        application.setStatus(status);
        applicationRepository.save(application);
    }

    public long countByCompanyId(Long companyId) {
        return applicationRepository.countByCompanyId(companyId);
    }

    public long countByCompanyIdAndStatus(Long companyId, Application.Status status) {
        return applicationRepository.countByCompanyIdAndStatus(companyId, status);
    }
}
