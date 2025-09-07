package com.cognizant.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.cognizant.entity.TimeSlot;

public interface TimeSlotRepository extends JpaRepository<TimeSlot, Long> {
    // You can add custom query methods here if needed
    // For example, to find time slots by availability ID:
    // List<TimeSlot> findByAvailabilityId(Long availabilityId);
}