package com.club.fund.controller;

import com.club.fund.common.Result;
import com.club.fund.entity.SysLog;
import com.club.fund.service.SysLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/log")
@RequiredArgsConstructor
public class SysLogController {

    private final SysLogService sysLogService;

    @GetMapping("/list")
    public Result<Page<SysLog>> getLogList(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String operation,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return Result.success(sysLogService.search(username, operation, status, pageable));
    }
}
