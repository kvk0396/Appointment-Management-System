package com.cognizant.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cognizant.entity.Consultation;

public interface ConsultationRepository extends JpaRepository<Consultation, Long> {

}
