package com.cognizant.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class TimeSlotResponseDTO {
    private Long timeSlotId;
    private String startTime;
    private String endTime;
    private LocalDate dateOfAppointment;
}
