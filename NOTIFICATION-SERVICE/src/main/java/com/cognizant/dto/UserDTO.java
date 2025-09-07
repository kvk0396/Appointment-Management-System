package com.cognizant.dto;
import com.cognizant.enums.Role;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
 
@Data
public class UserDTO {
 
    private Long userId;
 
    @NotBlank(message = "Name is required") // Ensures name is not empty or null
    private String name;
 
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format") // Validates email format
    private String email;
 
    @NotBlank(message = "Phone number is required")
    @Size(min = 10, max = 10, message = "Phone number must be 10 digits") // Validates phone number size
    private String phone;
 
    @NotNull(message = "Role must be specified")
    private Role role; // DOCTOR or PATIENT
}