package com.club.fund.controller;

import com.club.fund.common.Result;
import com.club.fund.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/upload")
@RequiredArgsConstructor
public class UploadController {

    private final FileService fileService;

    @PostMapping("/image")
    public Result<Map<String, Object>> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam(defaultValue = "voucher") String type) {
        return Result.success(fileService.uploadImage(file, type));
    }

    @PostMapping("/avatar")
    public Result<Map<String, Object>> uploadAvatar(@RequestParam("file") MultipartFile file) {
        return Result.success(fileService.uploadImage(file, "avatar"));
    }
}
