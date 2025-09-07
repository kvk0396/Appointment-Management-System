package com.cognizant.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cognizant.config.APIClient;
import com.cognizant.config.AvailabilityClient;
import com.cognizant.config.ConsultationClient;
import com.cognizant.dto.AppointmentDTO;
import com.cognizant.dto.AppointmentNotificationDTO;
import com.cognizant.dto.AppointmentRequestDto;
import com.cognizant.dto.AvailabilityDTO;
import com.cognizant.dto.ConsultationDTO;
import com.cognizant.dto.TimeSlotDto;
import com.cognizant.dto.UserDTO;
import com.cognizant.entity.Appointment;
import com.cognizant.entity.TimeSlot;
import com.cognizant.enums.Status;
import com.cognizant.mapper.AppointmentMapper;
import com.cognizant.repository.AppointmentRepository;
import com.cognizant.repository.TimeSlotRepository;
import com.cognizant.repository.UserRepository;
import com.cognizant.service.IDoctorService;

import jakarta.validation.Valid;

@Validated
@RestController
@RequestMapping("/api/v1/doctor")
public class DoctorController  {

	@Autowired
	IDoctorService doctorService;
	
	@Autowired
	APIClient apiClient;
	
	@Autowired
	AvailabilityClient availabilityClient;
	
	@Autowired
	ConsultationClient consultationClient;
	
	@Autowired
	AppointmentRepository appointmentRepository;
		
	@Autowired
	UserRepository userRepository;
	
	@PostMapping("/login")
	public ResponseEntity<String> login(@RequestParam String email){
		return new ResponseEntity<String>(doctorService.checkLogin(email), HttpStatus.OK);
	}
	
	@PostMapping("/register")
	public ResponseEntity<UserDTO> addUser(@Valid @RequestBody UserDTO user){
		return new ResponseEntity<UserDTO>(doctorService.addUser(user), HttpStatus.CREATED);
	}
	
	/**
     * Updates a doctor's profile.
     * Accessible only by users with the 'DOCTOR' role.
     * Additionally, a doctor can only update their OWN profile.
     *
     * @param id The ID of the doctor to update (from path variable).
     * @param user The updated user DTO.
     * @return Updated UserDTO.
     */
    @PutMapping("/update/{id}")
    @PreAuthorize("hasRole('DOCTOR') and @authService.getCurrentAuthenticatedUser().getUserId() == #id")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @Valid @RequestBody UserDTO user){
        // The @PreAuthorize annotation handles the security checks before this method executes.
        // If the checks pass, the method proceeds.
        // If they fail, Spring Security throws an AccessDeniedException.
        return new ResponseEntity<UserDTO>(doctorService.updateUser(id,user), HttpStatus.OK);
    }

    /**
     * Views all users (doctors and potentially patients if your DoctorService.viewAllUsers() returns all).
     * Accessible by both 'PATIENT' and 'DOCTOR' roles.
     *
     * @return A list of all UserDTOs.
     */
    @GetMapping("/viewAll")
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR')")
    public ResponseEntity<List<UserDTO>> viewAllUsers(){
        // The @PreAuthorize annotation handles the security checks.
        return new ResponseEntity<List<UserDTO>>(doctorService.viewAllUsers(), HttpStatus.OK);
    }
	
	
	
	
	@GetMapping("/appointments/{doctorId}")
	@PreAuthorize("hasRole('DOCTOR')")
	public ResponseEntity<List<AppointmentDTO>> viewDoctorAppointments(@PathVariable Long doctorId){
		return new ResponseEntity<List<AppointmentDTO>>(doctorService.viewAllDoctorAppointments(doctorId), HttpStatus.OK);
	}
	
	
	
//	@GetMapping("/doctor/{doctorId}/availabilities")
//	public ResponseEntity<List<AvailabilityDTO>> getAvailability(@PathVariable Long doctorId) {
//	    List<AvailabilityDTO> availabilityList = availabilityClient.getAvailability(doctorId);
//	    return ResponseEntity.ok(availabilityList);
//	}

//	@PostMapping("/doctor/availability")
//	public ResponseEntity<AvailabilityDTO> addAvailability(@RequestBody AvailabilityDTO availability){
//		return new ResponseEntity<AvailabilityDTO>(availabilityClient.addAvailability(availability), HttpStatus.OK);
//	}
	
//	@PutMapping("/availability/doctor/{doctorId}/date/{date}")
//    public ResponseEntity<AvailabilityDTO> updateAvailabilityByDoctorAndDate(
//            @PathVariable Long doctorId,
//            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
//            @RequestBody AvailabilityDTO updatedDto) {
//        AvailabilityDTO updated = availabilityClient.updateAvailabilityByDoctorAndDate(doctorId, date, updatedDto);
//        return ResponseEntity.ok(updated);
//    }
	
		
}
