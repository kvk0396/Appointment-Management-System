package com.cognizant.dto;


import java.time.LocalDate;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
public class AppointmentRequestDto {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long appointmentId;

    private Long patientId;

    private LocalDate dateOfAppointment;
    
    private Long doctorId;

    
    private TimeSlotDto timeSlot;

    
}
