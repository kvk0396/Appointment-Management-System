package com.cognizant.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import lombok.Data;

@Data
public class AvailabilityDTO {
    private Long id;
    private LocalDate date;
    private Long doctorId;
    private LocalTime startTime;
    private LocalTime endTime;
    private List<TimeSlotDTO> timeSlots;
}