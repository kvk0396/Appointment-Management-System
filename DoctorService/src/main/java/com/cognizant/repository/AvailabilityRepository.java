package com.cognizant.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cognizant.entity.Availability;

public interface AvailabilityRepository extends JpaRepository<Availability, Long> {

}
