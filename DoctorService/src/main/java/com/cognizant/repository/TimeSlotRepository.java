package com.cognizant.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cognizant.entity.TimeSlot;


public interface TimeSlotRepository extends JpaRepository<TimeSlot, Long> {

}
