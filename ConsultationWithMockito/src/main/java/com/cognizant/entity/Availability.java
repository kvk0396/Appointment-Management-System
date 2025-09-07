package com.cognizant.entity;
 
import jakarta.persistence.*;
import lombok.Data;
 
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
 
@Entity
@Data
public class Availability {
 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
 
    private LocalDate date;
 
    private LocalTime startTime;
 
    private LocalTime endTime;
 
    @ManyToOne
    @JoinColumn(name = "doctor_id")
    private User doctor;
 
    @OneToMany(mappedBy = "availability", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TimeSlot> timeSlots;
}