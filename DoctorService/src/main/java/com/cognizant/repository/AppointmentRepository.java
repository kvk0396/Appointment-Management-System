package com.cognizant.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cognizant.entity.Appointment;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

}
