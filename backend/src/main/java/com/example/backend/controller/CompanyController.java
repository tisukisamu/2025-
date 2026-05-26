package com.example.backend.controller;

import com.example.backend.dto.CompanyDTO;
import com.example.backend.entity.Company;
import com.example.backend.security.UserDetailsImpl;
import com.example.backend.service.CompanyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/companies")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CompanyController {

    private final CompanyService companyService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllCompanies() {
        List<Company> companies = companyService.getAllCompanies();
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", companies);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getCompanyById(@PathVariable Long id) {
        Company company = companyService.getCompanyById(id);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", company);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/my")
    public ResponseEntity<Map<String, Object>> getMyCompany(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        Company company = companyService.getCompanyByUserId(userDetails.getId());
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", company);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createCompany(
            @Valid @RequestBody CompanyDTO companyDTO,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Company company = companyService.createCompany(companyDTO, userDetails.getId());
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "企业创建成功，等待审核");
        response.put("data", company);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateCompany(
            @PathVariable Long id,
            @Valid @RequestBody CompanyDTO companyDTO) {
        Company company = companyService.updateCompany(id, companyDTO);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "更新成功");
        response.put("data", company);
        return ResponseEntity.ok(response);
    }
}
