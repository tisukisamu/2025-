package com.club.fund.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserUpdateRequest {

    private String realName;

    private String phone;

    private String email;

    private String avatar;

    private Long roleId;

    private Long clubId;
}
