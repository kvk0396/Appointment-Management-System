package com.cognizant.repository;

import com.cognizant.entity.Availability;
import com.cognizant.entity.TimeSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TimeSlotRepository extends JpaRepository<TimeSlot, Long> {
    List<TimeSlot> findByAvailability(Availability availability);
    TimeSlot findByAvailabilityAndTimeSlotId(Availability availability, Long timeSlotId);
}
         
         