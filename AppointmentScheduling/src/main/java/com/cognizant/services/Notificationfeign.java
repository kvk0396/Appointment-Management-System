package com.cognizant.services;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cognizant.dto.AppointmentResponseDTO;
import com.cognizant.entity.Appointment;

@FeignClient(url = "http://localhost:2467", value =  "NOTIFICATION-SERVICE")
public interface Notificationfeign {
	
	@PostMapping("/api/v1/SendMailForAppointmentStatus")
	public String sendMailForAppointmentStatus(@RequestBody AppointmentResponseDTO appointment);

}
