package com.example.backend.service;

import com.example.backend.dto.CompanyDTO;
import com.example.backend.entity.Company;
import com.example.backend.exception.BusinessException;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyRepository companyRepository;

    public List<Company> getAllCompanies() {
        return companyRepository.findAll();
    }

    public Company getCompanyById(Long id) {
        return companyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("企业不存在"));
    }

    public Company getCompanyByUserId(Long userId) {
        return companyRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("企业信息不存在"));
    }

    @Transactional
    public Company createCompany(CompanyDTO companyDTO, Long userId) {
        if (companyRepository.existsByName(companyDTO.getName())) {
            throw new BusinessException("企业名称已存在");
        }

        Company company = new Company();
        company.setName(companyDTO.getName());
        company.setDescription(companyDTO.getDescription());
        company.setIndustry(companyDTO.getIndustry());
        company.setScale(companyDTO.getScale());
        company.setAddress(companyDTO.getAddress());
        company.setContactPerson(companyDTO.getContactPerson());
        company.setContactPhone(companyDTO.getContactPhone());
        company.setContactEmail(companyDTO.getContactEmail());
        company.setLogoUrl(companyDTO.getLogoUrl());
        company.setUserId(userId);
        company.setStatus(Company.Status.PENDING);

        return companyRepository.save(company);
    }

    @Transactional
    public Company updateCompany(Long id, CompanyDTO companyDTO) {
        Company company = getCompanyById(id);
        company.setName(companyDTO.getName());
        company.setDescription(companyDTO.getDescription());
        company.setIndustry(companyDTO.getIndustry());
        company.setScale(companyDTO.getScale());
        company.setAddress(companyDTO.getAddress());
        company.setContactPerson(companyDTO.getContactPerson());
        company.setContactPhone(companyDTO.getContactPhone());
        company.setContactEmail(companyDTO.getContactEmail());
        company.setLogoUrl(companyDTO.getLogoUrl());
        return companyRepository.save(company);
    }

    @Transactional
    public void approveCompany(Long id) {
        Company company = getCompanyById(id);
        company.setStatus(Company.Status.APPROVED);
        companyRepository.save(company);
    }

    @Transactional
    public void rejectCompany(Long id) {
        Company company = getCompanyById(id);
        company.setStatus(Company.Status.REJECTED);
        companyRepository.save(company);
    }
}
