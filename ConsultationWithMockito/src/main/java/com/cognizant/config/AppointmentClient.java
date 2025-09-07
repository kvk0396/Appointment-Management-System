package com.cognizant.config;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.cognizant.dto.AppointmentResponseDTO;

@FeignClient(url = "http://localhost:8082", value =  "APPOINTMENT-SERVICE")
public interface AppointmentClient {
	@PutMapping("/api/v1/appointments/complete/{appointmentId}")
	public String updateAppointment(@PathVariable Long appointmentId);
}
