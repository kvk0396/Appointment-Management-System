package com.cognizant.dto;

import java.time.LocalDate;

import com.cognizant.entity.TimeSlot;
import com.cognizant.enums.Status;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
public class AppointmentDTO {

	
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long appointmentId;

   
	
    private LocalDate dateOfAppointment;
   
    private TimeSlotDto timeSlot;

    @Enumerated(EnumType.STRING)
    private Status status; // BOOKED, CANCELLED, COMPLETED

//    @OneToOne(mappedBy = "appointment", cascade = CascadeType.ALL)
//    private Consultation consultation;

	
}
