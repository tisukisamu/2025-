package com.example.backend.service;

import com.example.backend.entity.Application;
import com.example.backend.entity.Job;
import com.example.backend.repository.ApplicationRepository;
import com.example.backend.repository.CompanyRepository;
import com.example.backend.repository.JobRepository;
import com.example.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatisticsService {

    private final JobRepository jobRepository;
    private final ApplicationRepository applicationRepository;
    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;

    public Map<String, Object> getSystemOverview() {
        Map<String, Object> overview = new HashMap<>();
        overview.put("totalUsers", userRepository.count());
        overview.put("totalCompanies", companyRepository.count());
        overview.put("totalJobs", jobRepository.count());
        overview.put("activeJobs", jobRepository.findByStatus(Job.Status.ACTIVE).size());
        return overview;
    }

    public Map<String, Object> getCompanyOverview(Long companyId) {
        Map<String, Object> overview = new HashMap<>();
        overview.put("totalJobs", jobRepository.countByCompanyId(companyId));
        overview.put("activeJobs", jobRepository.countByCompanyIdAndStatus(companyId, Job.Status.ACTIVE));
        overview.put("totalApplications", applicationRepository.countByCompanyId(companyId));
        overview.put("pendingApplications", applicationRepository.countByCompanyIdAndStatus(companyId, Application.Status.PENDING));
        overview.put("interviewedApplications", applicationRepository.countByCompanyIdAndStatus(companyId, Application.Status.INTERVIEWED));
        overview.put("hiredApplications", applicationRepository.countByCompanyIdAndStatus(companyId, Application.Status.ACCEPTED));
        return overview;
    }

    public Map<String, Object> getRecruitmentTrend(Long companyId, LocalDate start, LocalDate end) {
        List<Object[]> dailyStats = applicationRepository.countByDateRange(companyId, start, end);

        Map<String, Object> trend = new HashMap<>();
        trend.put("dates", dailyStats.stream().map(r -> r[0]).collect(Collectors.toList()));
        trend.put("counts", dailyStats.stream().map(r -> r[1]).collect(Collectors.toList()));
        return trend;
    }

    public Map<String, Object> getTalentAnalysis(Long companyId) {
        List<Application> applications = applicationRepository.findByCompanyId(companyId);

        Map<String, Long> educationDistribution = applications.stream()
                .filter(a -> a.getResumeId() != null)
                .collect(Collectors.groupingBy(
                        a -> {
                            return "未知";
                        },
                        Collectors.counting()
                ));

        Map<String, Object> analysis = new HashMap<>();
        analysis.put("totalApplications", applications.size());
        analysis.put("educationDistribution", educationDistribution);
        return analysis;
    }
}
