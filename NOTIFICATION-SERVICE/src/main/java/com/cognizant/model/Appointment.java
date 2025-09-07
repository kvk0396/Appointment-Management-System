package com.cognizant.model;

import com.cognizant.enums.Status;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Appointment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long appointmentId;// changed to integer because of incompactibility by jparepo

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "patient_id")
	private User patient;

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "doctor_id")
	private User doctor;

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "time_slot_id")
	private TimeSlot timeSlot;

	@Enumerated(EnumType.STRING)
	private Status status = Status.BOOKED; // BOOKED, CANCELLED, COMPLETED

//    @OneToOne(mappedBy = "appointment", cascade = CascadeType.ALL)
//    private Consultation consultation;

}