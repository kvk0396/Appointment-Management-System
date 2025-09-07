package com.cognizant.entity;
 
import java.time.LocalDate;

import com.cognizant.enums.Status;
 
import jakarta.persistence.*;
import lombok.Data;
 
 
@Entity
@Data
public class Appointment {
 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long appointmentId;
 
    @ManyToOne
    @JoinColumn(name = "patient_id")
    private User patient;
 
    @ManyToOne
    @JoinColumn(name = "doctor_id")
    private User doctor;
 
    @Column(nullable=false)
    private LocalDate dateOfAppointment;
    
    @OneToOne
    @JoinColumn(name = "time_slot_id")
    private TimeSlot timeSlot;
 
    @Enumerated(EnumType.STRING)
    private Status status; // BOOKED, CANCELLED, COMPLETED
 
//    @OneToOne(mappedBy = "appointment", cascade = CascadeType.ALL)
//    private Consultation consultation;
 
    
}