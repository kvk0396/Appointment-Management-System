package com.cognizant.service;


import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import com.cognizant.dto.AvailabilityDTO;
import com.cognizant.entity.TimeSlot;

public interface IAvailabilityService {

    /**
     * Generates time slots for a given availability.
     * 
     * @param startTime Start time of availability.
     * @param endTime End time of availability.
     * @param availability The availability entity.
     * @return List of generated time slots.
     */
    List<TimeSlot> generateTimeSlots(LocalTime startTime, LocalTime endTime, com.cognizant.entity.Availability availability);

    /**
     * Saves availability for a doctor.
     * 
     * @param dto Availability DTO containing details.
     * @return Saved Availability DTO.
     */
    AvailabilityDTO saveAvailability(AvailabilityDTO dto);

    /**
     * Updates availability by doctor ID and date.
     * 
     * @param doctorId ID of the doctor.
     * @param date Date of availability.
     * @param updatedDto Updated Availability DTO.
     * @return Updated Availability DTO.
     */
    AvailabilityDTO updateAvailabilityByDoctorAndDate(Long doctorId, LocalDate date, AvailabilityDTO updatedDto);

    /**
     * Retrieves availability details for a given doctor.
     * 
     * @param doctorId ID of the doctor.
     * @return List of Availability DTOs.
     */
    List<AvailabilityDTO> getAvailabilityByDoctor(Long doctorId);
}
