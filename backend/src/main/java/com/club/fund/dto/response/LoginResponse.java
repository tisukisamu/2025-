package com.club.fund.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class LoginResponse {

    private String token;

    private UserResponse user;
}
