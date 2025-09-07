package com.cognizant.model;

import lombok.Data;

import java.util.List;

import com.cognizant.enums.Role;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

@Entity
@Data
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long userId;

	private String name;
	private String email;
	private String phone;

	@Enumerated(EnumType.STRING)
	private Role role; // DOCTOR or PATIENT

	@OneToMany(mappedBy = "patient")
	private List<Appointment> patientAppointments;

	@OneToMany(mappedBy = "doctor")
	private List<Appointment> doctorAppointments;

	@OneToMany(mappedBy = "doctor")
	private List<Availability> availabilities;

}