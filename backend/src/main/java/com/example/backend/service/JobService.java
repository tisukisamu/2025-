package com.example.backend.service;

import com.example.backend.dto.JobDTO;
import com.example.backend.entity.Company;
import com.example.backend.entity.Job;
import com.example.backend.exception.BusinessException;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.repository.CompanyRepository;
import com.example.backend.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class JobService {

    private final JobRepository jobRepository;
    private final CompanyRepository companyRepository;

    public List<Job> getJobsByCompanyId(Long companyId) {
        return jobRepository.findByCompanyId(companyId);
    }

    public Job getJobById(Long id) {
        return jobRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("职位不存在"));
    }

    public Page<Job> searchJobs(String title, String location, String education, 
                                String experience, Pageable pageable) {
        return jobRepository.searchJobs(title, location, education, experience, pageable);
    }

    @Transactional
    public Job createJob(JobDTO jobDTO, Long companyId) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("企业不存在"));

        if (company.getStatus() != Company.Status.APPROVED) {
            throw new BusinessException("企业未通过审核，无法发布职位");
        }

        Job job = new Job();
        job.setCompanyId(companyId);
        job.setTitle(jobDTO.getTitle());
        job.setDescription(jobDTO.getDescription());
        job.setRequirements(jobDTO.getRequirements());
        job.setSalaryMin(jobDTO.getSalaryMin());
        job.setSalaryMax(jobDTO.getSalaryMax());
        job.setLocation(jobDTO.getLocation());
        job.setJobType(jobDTO.getJobType());
        job.setEducation(jobDTO.getEducation());
        job.setExperience(jobDTO.getExperience());
        job.setCoverUrl(jobDTO.getCoverUrl());
        job.setStatus(Job.Status.ACTIVE);

        return jobRepository.save(job);
    }

    @Transactional
    public Job updateJob(Long id, JobDTO jobDTO) {
        Job job = getJobById(id);
        job.setTitle(jobDTO.getTitle());
        job.setDescription(jobDTO.getDescription());
        job.setRequirements(jobDTO.getRequirements());
        job.setSalaryMin(jobDTO.getSalaryMin());
        job.setSalaryMax(jobDTO.getSalaryMax());
        job.setLocation(jobDTO.getLocation());
        job.setJobType(jobDTO.getJobType());
        job.setEducation(jobDTO.getEducation());
        job.setExperience(jobDTO.getExperience());
        job.setCoverUrl(jobDTO.getCoverUrl());
        return jobRepository.save(job);
    }

    @Transactional
    public void closeJob(Long id) {
        Job job = getJobById(id);
        job.setStatus(Job.Status.CLOSED);
        jobRepository.save(job);
    }

    @Transactional
    public void deleteJob(Long id) {
        Job job = getJobById(id);
        jobRepository.delete(job);
    }
}
