package com.cognizant.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.cognizant.dto.AvailabilityDTO;
import com.cognizant.service.IAvailabilityService;

@RestController
@RequestMapping("/api/availability")
public class AvailabilityController {

	
	@Autowired
    IAvailabilityService availabilityService;

	/**
     * Adds new availability for a doctor.
     * Accessible only by users with the 'DOCTOR' role.
     * A doctor can ONLY add availability for themselves.
     * Assumes AvailabilityDTO has a 'doctorId' field.
     *
     * @param dto The AvailabilityDTO containing the doctorId and availability details.
     */
    @PostMapping("/")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<AvailabilityDTO> addAvailability(@RequestBody AvailabilityDTO dto) {
        // The @PreAuthorize handles the role and ownership checks.
        AvailabilityDTO saved = availabilityService.saveAvailability(dto);
        return ResponseEntity.ok(saved);
    }

    /**
     * Updates existing availability for a specific doctor and date.
     * Accessible only by users with the 'DOCTOR' role.
     * A doctor can ONLY update their own availability.
     *
     * @param doctorId The ID of the doctor whose availability is being updated.
     * @param date The specific date for the availability.
     * @param updatedDto The AvailabilityDTO with updated details.
     */
    @PutMapping("/doctor/{doctorId}/date/{date}")
    @PreAuthorize("hasRole('DOCTOR') ")
    public ResponseEntity<AvailabilityDTO> updateAvailabilityByDoctorAndDate(
            @PathVariable Long doctorId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestBody AvailabilityDTO updatedDto) {
        // The @PreAuthorize handles the role and ownership checks.
        AvailabilityDTO updated = availabilityService.updateAvailabilityByDoctorAndDate(doctorId, date, updatedDto);
        return ResponseEntity.ok(updated);
    }

    /**
     * Gets availability for a specific doctor.
     * Accessible by both 'DOCTOR' and 'PATIENT' roles.
     * - A Doctor can view their OWN availability (ownership check).
     * - A Patient can view ANY doctor's availability (no ownership check needed for patient).
     *
     * @param doctorId The ID of the doctor whose availability is to be retrieved.
     */
    @GetMapping("/{doctorId}")
    @PreAuthorize("hasAnyRole('DOCTOR', 'PATIENT')")
    public ResponseEntity<List<AvailabilityDTO>> getAvailability(@PathVariable Long doctorId) {
        // The @PreAuthorize handles the role and conditional ownership checks.
        List<AvailabilityDTO> availabilities = availabilityService.getAvailabilityByDoctor(doctorId);
        return ResponseEntity.ok(availabilities);
    }
}