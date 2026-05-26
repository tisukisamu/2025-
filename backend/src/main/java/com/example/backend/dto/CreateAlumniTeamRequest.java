package com.example.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateAlumniTeamRequest {
    @NotBlank(message = "小队名称不能为空")
    private String name;
    private String slogan;
    private String city;
    private Integer maxMembers;
}
