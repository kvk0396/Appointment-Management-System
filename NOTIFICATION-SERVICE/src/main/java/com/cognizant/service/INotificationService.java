package com.cognizant.service;

import java.util.List;

import com.cognizant.dto.AppointmentResponseDTO;
import com.cognizant.dto.UserDTO;

public interface INotificationService {
	
	public String userCreationAckMail(UserDTO user) throws Exception;

	public List<String> sendMailToUsers(AppointmentResponseDTO appointment) throws Exception;

	public String sendMailToPatient(AppointmentResponseDTO appointment) throws Exception;

	public String sendMailToDoctor(AppointmentResponseDTO appointment) throws Exception;

	public String sendMail(String mail, String subject, String body) throws Exception;
}
