package com.cognizant.repository;
 
import com.cognizant.entity.Availability;
import com.cognizant.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
 
@Repository
public interface AvailabilityRepository extends JpaRepository<Availability, Long> {
    List<Availability> findByDoctorUserIdAndDate(Long doctorId, LocalDate date);

	List<Availability> findByDoctorAndDate(User doctor, LocalDate date);
}
 