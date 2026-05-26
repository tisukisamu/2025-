package com.example.backend.dto.response;

import com.example.backend.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    private Long id;
    private String username;
    private String studentId;
    private String realName;
    private String phone;
    private String avatar;
    private User.Role role;
    private User.Status status;
    private Integer creditScore;
    private LocalDateTime createTime;

    public static UserDTO fromEntity(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .studentId(user.getStudentId())
                .realName(user.getRealName())
                .phone(user.getPhone())
                .avatar(user.getAvatar())
                .role(user.getRole())
                .status(user.getStatus())
                .creditScore(user.getCreditScore())
                .createTime(user.getCreateTime())
                .build();
    }
}
