package com.cognizant.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import lombok.Data;

@Data
public class AvailabilityResponseDTO {
    private Long id;
    private Long doctorId;
    private String doctorName;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private List<TimeSlotDTO> timeSlots;
}         
         