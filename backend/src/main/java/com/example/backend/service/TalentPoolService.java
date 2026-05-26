package com.example.backend.service;

import com.example.backend.entity.TalentGroup;
import com.example.backend.entity.TalentPool;
import com.example.backend.exception.BusinessException;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.repository.TalentGroupRepository;
import com.example.backend.repository.TalentPoolRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TalentPoolService {

    private final TalentPoolRepository talentPoolRepository;
    private final TalentGroupRepository talentGroupRepository;

    public List<TalentPool> getTalentPoolByCompanyId(Long companyId) {
        return talentPoolRepository.findByCompanyId(companyId);
    }

    public List<TalentPool> getTalentPoolByGroupId(Long companyId, Long groupId) {
        return talentPoolRepository.findByCompanyIdAndGroupId(companyId, groupId);
    }

    public TalentPool getTalentById(Long id) {
        return talentPoolRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("人才记录不存在"));
    }

    @Transactional
    public TalentPool addToPool(Long companyId, Long resumeId, Long userId) {
        if (talentPoolRepository.existsByCompanyIdAndResumeId(companyId, resumeId)) {
            throw new BusinessException("该人才已在人才库中");
        }

        TalentPool talent = new TalentPool();
        talent.setCompanyId(companyId);
        talent.setResumeId(resumeId);
        talent.setUserId(userId);
        talent.setStatus(TalentPool.Status.COLLECTED);
        return talentPoolRepository.save(talent);
    }

    @Transactional
    public TalentPool updateTalentTags(Long id, String tags) {
        TalentPool talent = getTalentById(id);
        talent.setTags(tags);
        return talentPoolRepository.save(talent);
    }

    @Transactional
    public TalentPool moveToGroup(Long id, Long groupId) {
        TalentPool talent = getTalentById(id);
        talent.setGroupId(groupId);
        return talentPoolRepository.save(talent);
    }

    @Transactional
    public void removeFromPool(Long id) {
        TalentPool talent = getTalentById(id);
        talentPoolRepository.delete(talent);
    }

    public List<TalentGroup> getGroupsByCompanyId(Long companyId) {
        return talentGroupRepository.findByCompanyId(companyId);
    }

    @Transactional
    public TalentGroup createGroup(Long companyId, String name, String description, String color) {
        TalentGroup group = new TalentGroup();
        group.setCompanyId(companyId);
        group.setName(name);
        group.setDescription(description);
        group.setColor(color);
        return talentGroupRepository.save(group);
    }

    @Transactional
    public TalentGroup updateGroup(Long id, String name, String description, String color) {
        TalentGroup group = talentGroupRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("分组不存在"));
        group.setName(name);
        group.setDescription(description);
        group.setColor(color);
        return talentGroupRepository.save(group);
    }

    @Transactional
    public void deleteGroup(Long id) {
        TalentGroup group = talentGroupRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("分组不存在"));
        talentGroupRepository.delete(group);
    }
}
