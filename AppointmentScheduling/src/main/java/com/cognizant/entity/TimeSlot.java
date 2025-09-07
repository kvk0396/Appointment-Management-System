 
package com.cognizant.entity;
 
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;
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
 
    @OneToOne(mappedBy = "timeSlot",cascade = CascadeType.ALL)
    private Appointment appointment;
 
    @ManyToOne
    @JoinColumn(name = "availability_id")
    private Availability availability;
}
 