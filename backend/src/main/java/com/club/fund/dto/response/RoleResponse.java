package com.club.fund.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RoleResponse {

    private Long id;

    private String roleName;

    private String roleCode;

    private String description;
}
