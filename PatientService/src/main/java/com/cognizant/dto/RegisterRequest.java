package com.cognizant.dto;

import com.cognizant.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    private String firstName;
    private String lastName;
    private String email; // This will be the username
    private String password;
    private String phone;
    private Role role; // To assign role upon registration
}