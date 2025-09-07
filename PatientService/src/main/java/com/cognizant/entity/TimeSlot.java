package com.cognizant.entity;

import lombok.Data;

import java.time.LocalTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;

@Entity
@Data
public class TimeSlot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long timeSlotId;

    private LocalTime startTime;

    private LocalTime endTime;

    private boolean isAvailable = true;

    @OneToOne(mappedBy = "timeSlot")
    private Appointment appointment;

    @ManyToOne
    @JoinColumn(name = "availability_id")
    private Availability availability;
}
