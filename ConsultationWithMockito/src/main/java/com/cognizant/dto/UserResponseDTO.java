package com.cognizant.dto;

import com.cognizant.enums.Role;
import lombok.Data;

@Data
public class UserResponseDTO {
    private Long userId;
    private String name;
    private String email;
    private String phone;
    private Role role;
}