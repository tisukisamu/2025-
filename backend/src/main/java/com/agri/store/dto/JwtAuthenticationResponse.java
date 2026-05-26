package com.agri.store.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
public class JwtAuthenticationResponse {
    private Long userId;
    private String accessToken;
    private String tokenType = "Bearer";
    private String username;
    private String role;
    private Boolean hasStore;
    private Integer storeStatus;

    public JwtAuthenticationResponse(String accessToken, String tokenType, String username, String role) {
        this.accessToken = accessToken;
        this.tokenType = tokenType;
        this.username = username;
        this.role = role;
    }

    public JwtAuthenticationResponse(Long userId, String accessToken, String tokenType, String username, String role) {
        this.userId = userId;
        this.accessToken = accessToken;
        this.tokenType = tokenType;
        this.username = username;
        this.role = role;
    }
}
