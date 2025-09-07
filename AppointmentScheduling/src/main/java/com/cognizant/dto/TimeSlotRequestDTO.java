package com.cognizant.dto;

import java.time.LocalTime;

import com.cognizant.enums.Slot;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TimeSlotRequestDTO {
	

	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long timeSlotId;

	@NotNull
    private LocalTime startTime;
 
	@NotNull
    private LocalTime endTime;
    
    
}
