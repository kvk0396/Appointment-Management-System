package com.cognizant.config;

import java.time.LocalDate;
import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable; // Import this
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.cognizant.dto.AvailabilityDTO;


@FeignClient(url = "http://localhost:8083" ,value="AVAILABILITY-SERVICE")
public interface AvailabilityClient {

	@GetMapping("/api/availability/{doctorId}")
	List<AvailabilityDTO> getAvailability(@PathVariable Long doctorId); // <--- Add @PathVariable here
	
	@PostMapping("/api/availability")
    public AvailabilityDTO addAvailability(AvailabilityDTO dto) ;
	
	@PutMapping("/api/availability/doctor/{doctorId}/date/{date}")

    AvailabilityDTO updateAvailabilityByDoctorAndDate(

        @PathVariable("doctorId") Long doctorId,

        @PathVariable("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,

        @RequestBody AvailabilityDTO updatedDto
    );
}