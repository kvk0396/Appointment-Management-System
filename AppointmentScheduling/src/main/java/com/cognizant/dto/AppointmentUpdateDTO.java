package com.cognizant.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AppointmentUpdateDTO {
	
	@NotNull
	private long appointmentId;
	
	private TimeSlotRequestDTO timeSlotRequestDTO;
	
	@NotNull
	private LocalDate dateOfAppointment;

}
