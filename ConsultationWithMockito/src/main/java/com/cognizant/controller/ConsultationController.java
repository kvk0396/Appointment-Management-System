package com.cognizant.controller;

import com.cognizant.dto.ConsultationResponseDTO;
import com.cognizant.service.IConsultationService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class ConsultationController {

    @Autowired
    private IConsultationService consultationService;

    // Create a new consultation
    @PostMapping("/consultation")
    public ResponseEntity<ConsultationResponseDTO> createConsultation(@RequestBody ConsultationResponseDTO consultationDTO) {
        ConsultationResponseDTO createdConsultation = consultationService.createConsultation(consultationDTO);
        return new ResponseEntity<>(createdConsultation, HttpStatus.CREATED);
    }

    // Update an existing consultation
    @PutMapping("/{id}")
    public ResponseEntity<ConsultationResponseDTO> updateConsultation(
            @PathVariable("id") Long consultationId,
            @RequestBody ConsultationResponseDTO consultationDTO) {

        ConsultationResponseDTO updatedConsultation = consultationService.updateConsultation(consultationId, consultationDTO);
        return new ResponseEntity<>(updatedConsultation, HttpStatus.OK);
    }

    // Get consultation by ID
    @GetMapping("/{id}")
    public ResponseEntity<ConsultationResponseDTO> getConsultationById(@PathVariable("id") Long consultationId) {
        ConsultationResponseDTO consultation = consultationService.getConsultationDTOById(consultationId);
        return new ResponseEntity<>(consultation, HttpStatus.OK);
    }
    
    @GetMapping("/by-appointment/{appointmentId}")
    public ResponseEntity<List<ConsultationResponseDTO>> getConsultationsByAppointmentId(
            @PathVariable("appointmentId") Long appointmentId) {
        List<ConsultationResponseDTO> consultations = consultationService.getConsultationsByAppointmentId(appointmentId);
        return new ResponseEntity<>(consultations, HttpStatus.OK);
    }
}