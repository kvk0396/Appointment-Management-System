package com.cognizant.controller;

import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cognizant.dto.AppointmentRequestDto;
import com.cognizant.dto.AppointmentResponseDTO;
import com.cognizant.dto.AppointmentUpdateDTO;
import com.cognizant.services.IAppointmentService;

import jakarta.validation.Valid;

@RequestMapping("/api/v1/appointments")
@RestController
public class AppointmentController {
	
    @Autowired
    IAppointmentService appointmentService;

    @PostMapping("/add")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<AppointmentResponseDTO> addAppointment(@Valid @RequestBody AppointmentRequestDto appointmentDto) {
        // The @PreAuthorize handles the role check.
        AppointmentResponseDTO addedAppointment = appointmentService.addAppointment(appointmentDto);
        return new ResponseEntity<>(addedAppointment, HttpStatus.OK);
    }

    /**
     * Views all appointments.
     * Accessible only by users with the 'PATIENT' role.
     * NOTE: In many real-world scenarios, a patient should only view THEIR OWN appointments.
     * If a patient should only view their own appointments, this endpoint should be removed
     * or refactored to filter by the authenticated patient's ID, and you might use
     * `@PreAuthorize("hasRole('PATIENT')")` combined with service-level filtering.
     * For now, this just checks for the role.
     */
    @GetMapping("/view")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<List<AppointmentResponseDTO>> viewAll() {
        // The @PreAuthorize handles the role check.
        return new ResponseEntity<>(appointmentService.viewAll(), HttpStatus.OK);
    }

    /**
     * Views a specific appointment by ID.
     * Accessible only by users with the 'PATIENT' role.
     * A patient can ONLY view their OWN appointments.
     * This uses the @authService and @appointmentService for the ownership check.
     *
     * @param appointmentId The ID of the appointment to view.
     */
    @GetMapping("/view/{appointmentId}")
    @PreAuthorize("hasRole('PATIENT') and @authService.getCurrentAuthenticatedUser().getUserId() == @appointmentService.getPatientIdForAppointment(#appointmentId)")
    public ResponseEntity<AppointmentResponseDTO> viewById(@Valid @PathVariable Long appointmentId) {
        // The @PreAuthorize handles the role and ownership checks.
        return new ResponseEntity<>(appointmentService.viewByAppointmentId(appointmentId), HttpStatus.OK);
    }

    /**
     * Updates an existing appointment.
     * Accessible only by users with the 'PATIENT' role.
     * A patient can ONLY update their OWN appointments.
     *
     * @param appointmentId The ID of the appointment to update.
     * @param appointmentUpdateDTO The DTO containing update information.
     */
    @PutMapping("/update/{appointmentId}")
    @PreAuthorize("hasRole('PATIENT') and @authService.getCurrentAuthenticatedUser().getUserId() == @appointmentService.getPatientIdForAppointment(#appointmentId)")
    public ResponseEntity<AppointmentResponseDTO> updateAppointment(@Valid @PathVariable Long appointmentId, @Valid @RequestBody AppointmentUpdateDTO appointmentUpdateDTO) {
        // The @PreAuthorize handles the role and ownership checks.
        return new ResponseEntity<>(appointmentService.updateAppointment(appointmentId, appointmentUpdateDTO), HttpStatus.OK);
    }

    /**
     * Cancels an appointment.
     * Accessible only by users with the 'PATIENT' role.
     * A patient can ONLY cancel their OWN appointments.
     *
     * @param appointmentId The ID of the appointment to cancel.
     */
    @PutMapping("/cancel/{appointmentId}")
    @PreAuthorize("hasRole('PATIENT') and @authService.getCurrentAuthenticatedUser().getUserId() == @appointmentService.getPatientIdForAppointment(#appointmentId)")
    public ResponseEntity<AppointmentResponseDTO> cancelAppointment(@Valid @PathVariable Long appointmentId) {
        // The @PreAuthorize handles the role and ownership checks.
        return new ResponseEntity<>(appointmentService.cancelAppointment(appointmentId), HttpStatus.OK);
    }
    
    @PutMapping("/complete/{appointmentId}")
    public ResponseEntity<AppointmentResponseDTO> completeAppointment(@Valid @PathVariable Long appointmentId){
    	return new ResponseEntity<AppointmentResponseDTO> (appointmentService.updateCompletion(appointmentId),HttpStatus.OK);
    }
    
}