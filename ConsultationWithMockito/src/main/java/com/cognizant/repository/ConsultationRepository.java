package com.cognizant.repository;

import com.cognizant.entity.Appointment;
import com.cognizant.entity.Consultation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConsultationRepository extends JpaRepository<Consultation, Long> {
    Optional<Consultation> findByAppointment(Appointment appointment);
    List<Consultation> findByAppointmentAppointmentId(Long appointmentId);
}