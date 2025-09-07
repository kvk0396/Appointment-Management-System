package com.cognizant.dto;

import java.time.LocalDate;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AppointmentRequestDto {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long appointmentId;
 
	@NotNull
    private Long patientId;
 
    @NotNull
    private Long doctorId;
    
    @NotNull
    private LocalDate dateOfAppointment;
 
    private TimeSlotRequestDTO timeSlot;
}
