package com.cognizant.dto;

import java.time.LocalTime;


import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

import lombok.Data;


@Data
public class TimeSlotDto {
    private Long timeSlotId;
    private LocalTime startTime;
    private LocalTime endTime;
    private boolean isAvailable;
}
