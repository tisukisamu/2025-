package com.club.fund.service;

import com.club.fund.entity.SysLog;
import com.club.fund.repository.SysLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SysLogService {

    private final SysLogRepository sysLogRepository;

    @Transactional
    public void saveLog(SysLog log) {
        sysLogRepository.save(log);
    }

    public Page<SysLog> getLogList(Pageable pageable) {
        return sysLogRepository.findAll(pageable);
    }

    public Page<SysLog> getLogByUserId(Long userId, Pageable pageable) {
        return sysLogRepository.findByUserId(userId, pageable);
    }

    public Page<SysLog> getLogByTimeRange(LocalDateTime startTime, LocalDateTime endTime, Pageable pageable) {
        return sysLogRepository.findByTimeRange(startTime, endTime, pageable);
    }

    public Page<SysLog> search(String username, String operation, Integer status, Pageable pageable) {
        return sysLogRepository.search(username, operation, status, pageable);
    }
}
