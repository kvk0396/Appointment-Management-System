package com.cognizant.service;

import java.util.List;

import com.cognizant.dto.AppointmentDTO;
import com.cognizant.dto.ConsultationDTO;
import com.cognizant.dto.UserDTO;

public interface IDoctorService {

	public UserDTO addUser(UserDTO user);
	public UserDTO updateUser(Long id,UserDTO user);
	public List<UserDTO> viewAllUsers();
	List<AppointmentDTO> viewAllDoctorAppointments(Long doctorId);
	UserDTO viewDoctorById(Long patientId);
	public String checkLogin(String email);
	
}
