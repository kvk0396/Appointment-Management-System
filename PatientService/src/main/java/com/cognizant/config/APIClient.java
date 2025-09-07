package com.cognizant.config;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.cognizant.dto.AppointmentNotificationDTO;
import com.cognizant.dto.UserDTO;
import com.cognizant.entity.Appointment;

@FeignClient(url = "http://localhost:2467" ,value="NOTIFICATION-SERVICE")
public interface APIClient {

	@PostMapping("/api/v1/SendMailForUserRegistry")
	public ResponseEntity<String> sendMailForUserRegistry(@RequestBody UserDTO user);
	
}
