package com.cognizant.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import com.cognizant.dto.AppointmentResponseDTO;
import com.cognizant.dto.UserDTO;
import com.cognizant.repository.UserRepository;
import com.cognizant.service.INotificationService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1")
@Slf4j
public class NotificationController {

	@Autowired
	UserRepository userRepository;
	
	@Autowired
	INotificationService iNotificationService;

	@PostMapping("/SendMailForAppointmentStatus")
	public ResponseEntity<List<String>> sendMailForAppointmentStatus(@RequestBody AppointmentResponseDTO appointment) throws Exception {

       log.info("Received request to send appointment status email: {}", appointment);


       log.info("Emails sent successfully to users doctor: {} patient{}", appointment.getDoctor(),appointment.getPatient());


		return new ResponseEntity<List<String>>(iNotificationService.sendMailToUsers(appointment), HttpStatus.OK);
	}
	
	@PostMapping("/SendMailForUserRegistry")
	public ResponseEntity<String> sendMailForUserRegistry(@RequestBody UserDTO user) throws Exception {
		log.info("User registry ack mail sent to user: {}",user);
		return new ResponseEntity<String>(iNotificationService.userCreationAckMail(user), HttpStatus.OK);
	}
	
	@PostMapping("/adduser")
	public String adduser(@RequestBody UserDTO user) throws Exception {
		//UserDTO u1= userRepository.save(user);
		iNotificationService.userCreationAckMail(user);
		return "Mail sent";
	}
}
