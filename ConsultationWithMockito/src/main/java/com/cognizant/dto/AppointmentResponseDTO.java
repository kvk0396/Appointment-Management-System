package com.cognizant.dto;
import java.time.LocalDate;

import com.cognizant.enums.Status;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.Data;

@Data
public class AppointmentResponseDTO {
 
	
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long appointmentId;
 
    private LocalDate dateOfAppointment;
    
    private TimeSlotResponseDTO timeSlot;
 
    @Enumerated(EnumType.STRING)
    private Status status; // BOOKED, CANCELLED, COMPLETED
 
    private UserDTO patient;
 
    private UserDTO doctor;
    
	
}