package com.club.fund.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "upload")
public class UploadConfig {

    private String path;

    private Long maxSize;

    private String allowedTypes;
}
