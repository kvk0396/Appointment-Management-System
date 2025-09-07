package com.cognizant.dto;

import java.time.LocalTime;



import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
public class TimeSlotRequestDTO {
	

	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long timeSlotId;

    private LocalTime startTime;
 
    private LocalTime endTime;
    
    
}
