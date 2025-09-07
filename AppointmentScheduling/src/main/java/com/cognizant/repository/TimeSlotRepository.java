package com.cognizant.repository;

import java.time.LocalTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cognizant.entity.TimeSlot;

public interface TimeSlotRepository extends JpaRepository<TimeSlot, Long>{

	Optional<TimeSlot> findByStartTimeAndAvailabilityId(LocalTime startTime, Long availabilityId);

}
