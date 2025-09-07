package com.cognizant.config;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.cognizant.dto.ConsultationDTO;

@FeignClient(url = "http://localhost:8084" ,value="CONSULTATION-SERVICE")
public interface ConsultationClient {

	@PostMapping("/api/v1/consultation")
    public ConsultationDTO createConsultation(@RequestBody ConsultationDTO consultationDTO);
	
	@PutMapping("/api/v1/{id}")
    public ConsultationDTO updateConsultation(@PathVariable("id") Long consultationId,@RequestBody ConsultationDTO consultationDTO);
	
	@GetMapping("/api/v1/{id}")
    public ConsultationDTO getConsultationById(@PathVariable("id") Long consultationId);
	
}
