package com.cognizant.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

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
import com.cognizant.service.IPatientService;

import jakarta.validation.Valid;

@Validated
@RestController
@RequestMapping("/api/v1/patient")
public class PatientController  {

	@Autowired
	IPatientService patientService;
	
	@Autowired
	APIClient apiClient;
	
	@Autowired
	AvailabilityClient availabilityClient;
	
	@Autowired
	ConsultationClient consultationClient;
	
	@Autowired
	AppointmentRepository appointmentRepository;
		
	@PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody Map<String, String> payload){
        String email = payload.get("email");
        return new ResponseEntity<String>(patientService.checkLogin(email), HttpStatus.OK);
    }

    /**
     * Handles patient registration.
     * This endpoint should be publicly accessible as it's for creating a new user.
     * Security is handled by the API Gateway's RouterValidator and this service's SecurityConfig.
     * No @PreAuthorize is needed here.
     */
    @PostMapping("/register")
    public ResponseEntity<UserDTO> addUser(@Valid @RequestBody UserDTO user){
        return new ResponseEntity<UserDTO>(patientService.addUser(user), HttpStatus.CREATED);
    }

    /**
     * Updates a patient's profile.
     * Accessible ONLY by users with the 'PATIENT' role.
     * Additionally, a patient can ONLY update THEIR OWN profile.
     *
     * @param id The ID of the patient to update (from path variable).
     * @param user The updated user DTO.
     * @return Updated UserDTO.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('PATIENT') and @authService.getCurrentAuthenticatedUser().getUserId() == #id")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @Valid @RequestBody UserDTO user){
        // The @PreAuthorize annotation handles the security checks before this method executes.
        // If the checks pass, the method proceeds.
        // If they fail, Spring Security throws an AccessDeniedException (403 Forbidden).
        return new ResponseEntity<UserDTO>(patientService.updateUser(id,user), HttpStatus.OK);
    }

    /**
     * Views all users (patients, and potentially doctors if your service returns them).
     *
     * IMPORTANT CONSIDERATION: Typically, patients should NOT be able to view *all* users
     * in a system for privacy reasons. This method is usually restricted to ADMIN or DOCTOR roles.
     *
     * However, based on your request "allow all the methods only for patients",
     * this method is restricted to the 'PATIENT' role. If a patient should only
     * view their *own* details, you would remove this method or refactor it to `viewMyProfile()`.
     * If this is truly intended for patients to see a list of *other* patients or doctors,
     * then `hasRole('PATIENT')` is correct.
     *
     * @return A list of all UserDTOs.
     */
    @GetMapping("/viewAll")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<List<UserDTO>> viewAllUsers(){
        // The @PreAuthorize annotation handles the security checks.
        return new ResponseEntity<List<UserDTO>>(patientService.viewAllUsers(), HttpStatus.OK);
    }

    /**
     * Views a patient's profile by ID.
     * Accessible ONLY by users with the 'PATIENT' role.
     * Additionally, a patient can ONLY view THEIR OWN profile.
     *
     * @param patientId The ID of the patient to view (from path variable).
     * @return UserDTO of the requested patient.
     */
    @GetMapping("/viewById/{id}")
    @PreAuthorize("hasRole('PATIENT') and @authService.getCurrentAuthenticatedUser().getUserId() == #id")
    public ResponseEntity<UserDTO> viewById(@PathVariable("id") Long id){ // Renamed to 'id' for consistency with @PathVariable
        // The @PreAuthorize annotation handles the security checks.
        return new ResponseEntity<UserDTO>(patientService.viewPatientById(id), HttpStatus.OK);
    }

    /**
     * Views all appointments for a specific patient.
     * Accessible ONLY by users with the 'PATIENT' role.
     * Additionally, a patient can ONLY view appointments for THEIR OWN patient ID.
     *
     * @param patientId The ID of the patient whose appointments are to be viewed.
     * @return A list of AppointmentDTOs for the specified patient.
     */
    @GetMapping("/patient-appointments/{patientId}")
    @PreAuthorize("hasRole('PATIENT') and @authService.getCurrentAuthenticatedUser().getUserId() == #patientId")
    public ResponseEntity<List<AppointmentDTO>> viewPatientAppointments(@PathVariable Long patientId){
        // The @PreAuthorize annotation handles the security checks.
        return new ResponseEntity<List<AppointmentDTO>>(patientService.viewAllPatientAppointments(patientId), HttpStatus.OK);
    }

    // Example for a delete method (if you uncomment it later)
    // @DeleteMapping("/user/{id}")
    // @PreAuthorize("hasRole('ADMIN') or (hasRole('PATIENT') and @authService.getCurrentAuthenticatedUser().getUserId() == #id)")
    // public ResponseEntity<String> deleteUser(@PathVariable Long id){
    //     // A patient can delete their own account, or an admin can delete any account.
    //     return new ResponseEntity<String>(patientService.deleteUser(id), HttpStatus.OK);
    // }
	
//	@GetMapping("/users/{doctorId}/availabilities")
//	public ResponseEntity<List<AvailabilityDTO>> getAvailability(@PathVariable Long doctorId) {
//	    List<AvailabilityDTO> availabilityList = availabilityClient.getAvailability(doctorId);
//	    return ResponseEntity.ok(availabilityList);
//	}
//	
//	@GetMapping("/users/consultation-record/{id}")
//    public ResponseEntity<ConsultationDTO> getConsultationById(@PathVariable("id") Long consultationId) {
//		return new ResponseEntity<ConsultationDTO>(consultationClient.getConsultationById(consultationId), HttpStatus.OK);
//	}
	
	
}
