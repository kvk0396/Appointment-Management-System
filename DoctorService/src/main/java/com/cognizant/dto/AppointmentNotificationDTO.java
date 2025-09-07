package com.cognizant.dto;

import lombok.Data;

@Data
public class AppointmentNotificationDTO {
    private Long appointmentId;
    private String status;

    private UserDTO doctor;
    private UserDTO patient;
    private TimeSlotDto timeSlot;
}