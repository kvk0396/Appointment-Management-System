package com.cognizant.service;

import java.util.List;

import com.cognizant.dto.AppointmentDTO;
import com.cognizant.dto.ConsultationDTO;
import com.cognizant.dto.UserDTO;

public interface IPatientService {

	public UserDTO addUser(UserDTO user);
	public UserDTO updateUser(Long id,UserDTO user);
	public List<UserDTO> viewAllUsers();
	public List<AppointmentDTO> viewAllPatientAppointments(Long patientId);
	public UserDTO viewPatientById(Long appointmentId);
	public String checkLogin(String email);
	
}
