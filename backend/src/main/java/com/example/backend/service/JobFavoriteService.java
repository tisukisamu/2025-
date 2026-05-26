package com.example.backend.service;

import com.example.backend.entity.Job;
import com.example.backend.entity.JobFavorite;
import com.example.backend.exception.BusinessException;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.repository.JobFavoriteRepository;
import com.example.backend.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class JobFavoriteService {

    private final JobFavoriteRepository jobFavoriteRepository;
    private final JobRepository jobRepository;

    @Transactional
    public void addFavorite(Long userId, Long jobId) {
        if (jobFavoriteRepository.existsByUserIdAndJobId(userId, jobId)) {
            throw new BusinessException("该职位已收藏");
        }
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("职位不存在"));
        if (job.getStatus() != Job.Status.ACTIVE) {
            throw new BusinessException("仅可收藏在线职位");
        }
        JobFavorite favorite = new JobFavorite();
        favorite.setUserId(userId);
        favorite.setJobId(jobId);
        jobFavoriteRepository.save(favorite);
    }

    @Transactional
    public void removeFavorite(Long userId, Long jobId) {
        JobFavorite favorite = jobFavoriteRepository.findByUserIdAndJobId(userId, jobId)
                .orElseThrow(() -> new ResourceNotFoundException("收藏记录不存在"));
        jobFavoriteRepository.delete(favorite);
    }

    public List<Job> getMyFavoriteJobs(Long userId) {
        List<JobFavorite> favorites = jobFavoriteRepository.findByUserIdOrderByCreatedAtDesc(userId);
        List<Long> jobIds = favorites.stream().map(JobFavorite::getJobId).toList();
        if (jobIds.isEmpty()) {
            return List.of();
        }
        List<Job> jobs = jobRepository.findAllById(jobIds);
        jobs.sort((a, b) -> Long.compare(jobIds.indexOf(a.getId()), jobIds.indexOf(b.getId())));
        return jobs;
    }

    public Set<Long> getMyFavoriteJobIds(Long userId) {
        List<JobFavorite> favorites = jobFavoriteRepository.findByUserId(userId);
        Set<Long> result = new HashSet<>();
        favorites.forEach(item -> result.add(item.getJobId()));
        return result;
    }
}
