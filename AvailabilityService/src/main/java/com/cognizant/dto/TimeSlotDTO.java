package com.cognizant.dto;

import java.time.LocalTime;
import lombok.Data;

@Data
public class TimeSlotDTO {
    private Long timeSlotId;
    private LocalTime startTime;
    private LocalTime endTime;
    private boolean isAvailable;
}