package com.agri.store.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.OperatingSystemMXBean;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class HealthController {

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", LocalDateTime.now());
        health.put("application", "agri-store-backend");
        return ResponseEntity.ok(health);
    }

    @GetMapping("/system/info")
    public ResponseEntity<Map<String, Object>> systemInfo() {
        Map<String, Object> info = new HashMap<>();

        // Memory info
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        long heapUsed = memoryBean.getHeapMemoryUsage().getUsed() / 1024 / 1024;
        long heapMax = memoryBean.getHeapMemoryUsage().getMax() / 1024 / 1024;
        long nonHeapUsed = memoryBean.getNonHeapMemoryUsage().getUsed() / 1024 / 1024;

        Map<String, Object> memory = new HashMap<>();
        memory.put("heapUsed", heapUsed + " MB");
        memory.put("heapMax", heapMax + " MB");
        memory.put("heapUsage", String.format("%.2f%%", (double) heapUsed / heapMax * 100));
        memory.put("nonHeapUsed", nonHeapUsed + " MB");
        info.put("memory", memory);

        // OS info
        OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
        Map<String, Object> os = new HashMap<>();
        os.put("name", osBean.getName());
        os.put("version", osBean.getVersion());
        os.put("arch", osBean.getArch());
        os.put("availableProcessors", osBean.getAvailableProcessors());
        info.put("os", os);

        // JVM info
        Runtime runtime = Runtime.getRuntime();
        Map<String, Object> jvm = new HashMap<>();
        jvm.put("javaVersion", System.getProperty("java.version"));
        jvm.put("freeMemory", runtime.freeMemory() / 1024 / 1024 + " MB");
        jvm.put("totalMemory", runtime.totalMemory() / 1024 / 1024 + " MB");
        jvm.put("maxMemory", runtime.maxMemory() / 1024 / 1024 + " MB");
        info.put("jvm", jvm);

        info.put("timestamp", LocalDateTime.now());

        return ResponseEntity.ok(info);
    }
}
