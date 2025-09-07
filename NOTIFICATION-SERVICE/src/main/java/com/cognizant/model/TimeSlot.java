package com.cognizant.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalTime;

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