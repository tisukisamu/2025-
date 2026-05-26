package com.example.backend.controller;

import com.example.backend.common.Result;
import com.example.backend.entity.ServicePackage;
import com.example.backend.service.ServicePackageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/services")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ServicePackageController {

    private final ServicePackageService servicePackageService;

    @GetMapping
    public Result<List<ServicePackage>> getActivePackages() {
        List<ServicePackage> packages = servicePackageService.getActivePackages();
        return Result.success(packages);
    }

    @GetMapping("/type/{type}")
    public Result<List<ServicePackage>> getPackagesByType(@PathVariable String type) {
        List<ServicePackage> packages = servicePackageService.getPackagesByType(type);
        return Result.success(packages);
    }

    @GetMapping("/{id}")
    public Result<ServicePackage> getPackageById(@PathVariable Long id) {
        ServicePackage pkg = servicePackageService.getPackageById(id);
        return Result.success(pkg);
    }

    @PostMapping
    public Result<ServicePackage> createPackage(@RequestBody ServicePackage servicePackage) {
        ServicePackage pkg = servicePackageService.createPackage(servicePackage);
        return Result.success("创建成功", pkg);
    }

    @PutMapping("/{id}")
    public Result<ServicePackage> updatePackage(@PathVariable Long id, @RequestBody ServicePackage servicePackage) {
        ServicePackage pkg = servicePackageService.updatePackage(id, servicePackage);
        return Result.success("更新成功", pkg);
    }

    @DeleteMapping("/{id}")
    public Result<Void> deletePackage(@PathVariable Long id) {
        servicePackageService.deletePackage(id);
        return Result.success("删除成功", null);
    }

    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        servicePackageService.updateStatus(id, status);
        return Result.success("状态更新成功", null);
    }
}
