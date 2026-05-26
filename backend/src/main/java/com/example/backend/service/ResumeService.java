package com.example.backend.service;

import com.example.backend.dto.ResumeDTO;
import com.example.backend.entity.Resume;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.repository.ResumeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ResumeService {

    private final ResumeRepository resumeRepository;

    public List<Resume> getResumesByUserId(Long userId) {
        return resumeRepository.findByUserId(userId);
    }

    public Resume getResumeById(Long id) {
        return resumeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("简历不存在"));
    }

    @Transactional
    public Resume createResume(ResumeDTO resumeDTO, Long userId) {
        Resume resume = new Resume();
        resume.setUserId(userId);
        resume.setName(resumeDTO.getName());
        resume.setGender(resumeDTO.getGender());
        resume.setAge(resumeDTO.getAge());
        resume.setPhone(resumeDTO.getPhone());
        resume.setEmail(resumeDTO.getEmail());
        resume.setEducation(resumeDTO.getEducation());
        resume.setExperience(resumeDTO.getExperience());
        resume.setSkills(resumeDTO.getSkills());
        resume.setWorkExperience(resumeDTO.getWorkExperience());
        resume.setProjectExperience(resumeDTO.getProjectExperience());
        resume.setEducationExperience(resumeDTO.getEducationExperience());
        return resumeRepository.save(resume);
    }

    @Transactional
    public Resume updateResume(Long id, ResumeDTO resumeDTO) {
        Resume resume = getResumeById(id);
        resume.setName(resumeDTO.getName());
        resume.setGender(resumeDTO.getGender());
        resume.setAge(resumeDTO.getAge());
        resume.setPhone(resumeDTO.getPhone());
        resume.setEmail(resumeDTO.getEmail());
        resume.setEducation(resumeDTO.getEducation());
        resume.setExperience(resumeDTO.getExperience());
        resume.setSkills(resumeDTO.getSkills());
        resume.setWorkExperience(resumeDTO.getWorkExperience());
        resume.setProjectExperience(resumeDTO.getProjectExperience());
        resume.setEducationExperience(resumeDTO.getEducationExperience());
        return resumeRepository.save(resume);
    }

    @Transactional
    public void deleteResume(Long id) {
        Resume resume = getResumeById(id);
        resumeRepository.delete(resume);
    }

    public List<Resume> filterResumes(String keyword, String education, String experience) {
        return resumeRepository.filterResumes(keyword, education, experience);
    }
}
