package com.cognizant.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.cognizant.entity.Availability;

public interface AvailabilityRepository extends JpaRepository<Availability, Long> {
    List<Availability> findByDoctorUserId(Long doctorId);
    Optional<Availability> findByDoctorUserIdAndDate(Long doctorId, LocalDate date);
}