package com.club.fund.dto.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class UserResponse {

    private Long id;

    private String username;

    private String realName;

    private String studentId;

    private String phone;

    private String email;

    private String avatar;

    private RoleResponse role;

    private Integer status;

    private LocalDateTime createTime;

    private String position;

    private LocalDateTime joinTime;

    private List<String> permissions;
}
