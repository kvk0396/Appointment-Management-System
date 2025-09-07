package com.cognizant.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

import com.cognizant.enums.Role;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
@Data
@AllArgsConstructor
@Table(name="users")
public class User {

	

	public User() {
		// TODO Auto-generated constructor stub
	}

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @NotBlank(message = "Name is required") // Bean Validation: Ensures the field is not null and not empty
    @Column(nullable = false)             //JPA: Makes column not nullable
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format") // Bean Validation: Validates email format
    @Column(nullable = false, unique = true)  // JPA: Makes column not nullable and unique
    private String email;

    @NotBlank(message = "Phone number is required")
    @Size(min=10 , max=10 , message="Phone number must be 10 digits")
    @Column(nullable = false, length = 10,unique=true)  //JPA: Not Null and Length is 10.
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull(message = "Role must be specified")
    private Role role; // DOCTOR or PATIENT
    
    @OneToMany(mappedBy = "patient")
    private List<Appointment> patientAppointments;

    @OneToMany(mappedBy = "doctor")
    private List<Appointment> doctorAppointments;

    @OneToMany(mappedBy = "doctor")
    private List<Availability> availabilities;

    
}
