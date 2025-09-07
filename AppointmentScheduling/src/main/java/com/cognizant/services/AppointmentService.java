package com.cognizant.services;
 
import java.time.LocalDateTime;
import java.util.List;
 
import org.slf4j.Logger; // Import Logger
import org.slf4j.LoggerFactory; // Import LoggerFactory
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.cognizant.dto.AppointmentRequestDto;
import com.cognizant.dto.AppointmentResponseDTO;
import com.cognizant.dto.AppointmentUpdateDTO;
import com.cognizant.entity.Appointment;
import com.cognizant.entity.Availability;
import com.cognizant.entity.TimeSlot;
import com.cognizant.entity.User;
import com.cognizant.enums.Role;
import com.cognizant.enums.Status;
import com.cognizant.exceptions.InvalidAppointmentException;
import com.cognizant.exceptions.TimeSlotUnavailableException;
import com.cognizant.exceptions.UserNotFoundException;
import com.cognizant.mapper.AppointmentMapper;
import com.cognizant.mapper.UserMapper;
import com.cognizant.repository.AppointmentRepository;
import com.cognizant.repository.TimeSlotRepository;
import com.cognizant.repository.UserRepository;
 
@Service
public class AppointmentService implements IAppointmentService {
 
    // Initialize Logger
    private static final Logger log = LoggerFactory.getLogger(AppointmentService.class);
 
    @Autowired
    private AppointmentRepository appointmentRepo;
    @Autowired
    Notificationfeign notificationfeign;
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private TimeSlotRepository timeSlotRepo;


 
    @Override
    public List<AppointmentResponseDTO> viewAll() {
        log.info("Attempting to view all appointments.");
        List<Appointment> appts = appointmentRepo.findAll();
        log.info("Successfully retrieved {} appointments.", appts.size());
        return AppointmentMapper.convertToDtoList(appts);
    }
 
    
    @Override
    public AppointmentResponseDTO viewByAppointmentId(Long appointmentId) {
        log.info("Attempting to view appointment with ID: {}", appointmentId);
        Appointment appointment = appointmentRepo.findById(appointmentId)
                .orElseThrow(() -> {
                    log.warn("Appointment not found with ID: {}", appointmentId);
                    return new InvalidAppointmentException("Appointment not found with ID: " + appointmentId);
                });
 
        AppointmentResponseDTO appointmentResponse = AppointmentMapper.convertToDto(appointment);
        appointmentResponse.setPatient(UserMapper.convertToDto(appointment.getPatient()));
        appointmentResponse.setDoctor(UserMapper.convertToDto(appointment.getDoctor()));
        log.info("Successfully retrieved appointment with ID: {}", appointmentId);
        return appointmentResponse;
    }
 
    
    @Override
    public AppointmentResponseDTO addAppointment(AppointmentRequestDto appointmentDto) {
        log.info("Attempting to add a new appointment for patient ID: {} and doctor ID: {}", appointmentDto.getPatientId(), appointmentDto.getDoctorId());
 
        User doctor = userRepo.findById(appointmentDto.getDoctorId())
                .orElseThrow(() -> {
                    log.warn("Doctor not found with ID: {}", appointmentDto.getDoctorId());
                    return new UserNotFoundException("Doctor not found with ID: " + appointmentDto.getDoctorId());
                });
        
        if (!doctor.getRole().equals(Role.DOCTOR)) {
            log.warn("User with ID: {} is not a doctor.", appointmentDto.getDoctorId());
            throw new InvalidAppointmentException("Doctor not found !");
        }
 
        User patient = userRepo.findById(appointmentDto.getPatientId())
                .orElseThrow(() -> {
                    log.warn("Patient not found with ID: {}", appointmentDto.getPatientId());
                    return new UserNotFoundException("Patient not found with ID: " + appointmentDto.getPatientId());
                });
 
        if(!patient.getRole().equals(Role.PATIENT)) {
            log.warn("User with ID: {} is not a patient.", appointmentDto.getPatientId());
        	throw new InvalidAppointmentException("Patient not found !");
        }
        
        LocalDateTime proposedAppointmentDateTime = LocalDateTime.of(
                appointmentDto.getDateOfAppointment(),
                appointmentDto.getTimeSlot().getStartTime()
            );
            if (proposedAppointmentDateTime.isBefore(LocalDateTime.now())) {
                log.warn("Attempt to book appointment for a past date/time: {}", proposedAppointmentDateTime);
                throw new InvalidAppointmentException("Cannot book an appointment for a date or time that has already passed.");
            }
        
        
        if(appointmentDto.getTimeSlot().getStartTime().isAfter(appointmentDto.getTimeSlot().getEndTime())) {
            log.warn("Invalid time slot: Start time {} exceeds end time {}", appointmentDto.getTimeSlot().getStartTime(), appointmentDto.getTimeSlot().getEndTime());
        	throw new TimeSlotUnavailableException("! Start time exceeds end time !");
        }
        
        List<Availability> availabilities = doctor.getAvailabilities();
        Availability selectedAvailability = availabilities.stream()
                .filter(a -> a.getDate().equals(appointmentDto.getDateOfAppointment()))
                .findFirst()
                .orElseThrow(() -> {
                    log.warn("No availability found for doctor ID: {} on date: {}", doctor.getUserId(), appointmentDto.getDateOfAppointment());
                    return new InvalidAppointmentException("No availability found for the selected date.");
                });
 
        TimeSlot timeSlot = timeSlotRepo.findByStartTimeAndAvailabilityId(
                appointmentDto.getTimeSlot().getStartTime(), selectedAvailability.getId())
                .orElseThrow(() -> {
                    log.warn("Time slot not found for start time: {} and availability ID: {}", appointmentDto.getTimeSlot().getStartTime(), selectedAvailability.getId());
                    return new InvalidAppointmentException("Time slot not found for the selected time.");
                });
 
        if (!timeSlot.isAvailable()) {
            log.warn("Selected time slot for doctor ID: {} at {} on {} is not available.", doctor.getUserId(), appointmentDto.getTimeSlot().getStartTime(), appointmentDto.getDateOfAppointment());
            throw new TimeSlotUnavailableException("Selected time slot is not available.");
        }
 
        Appointment appointment = new Appointment();
        appointment.setDoctor(doctor);
        appointment.setPatient(patient);
        appointment.setDateOfAppointment(appointmentDto.getDateOfAppointment());
        appointment.setStatus(Status.BOOKED);
        appointment.setTimeSlot(timeSlot);
 
        timeSlot.setAppointment(appointment);
        timeSlot.setAvailable(false);
 
        appointmentRepo.save(appointment);
        log.info("Appointment with ID: {} successfully booked for patient ID: {} with doctor ID: {} at {}", appointment.getAppointmentId(), patient.getUserId(), doctor.getUserId(), appointment.getTimeSlot().getStartTime());
        notificationfeign.sendMailForAppointmentStatus( AppointmentMapper.convertToDto(appointment));
        log.info("Notification sent for newly booked appointment with ID: {}", appointment.getAppointmentId());
        return AppointmentMapper.convertToDto(appointment);
    }
 
    
    @Override
    public AppointmentResponseDTO updateAppointment(Long appointmentId,AppointmentUpdateDTO appointmentUpdateDTO) {
        log.info("Attempting to update appointment with ID: {}", appointmentId);
        Appointment appointment = appointmentRepo.findById(appointmentId)
                .orElseThrow(() -> {
                    log.warn("Appointment not found with ID: {}", appointmentId);
                    return new InvalidAppointmentException("Appointment not found with ID: " + appointmentId);
                });
 
        User doctor = userRepo.findById(appointment.getDoctor().getUserId())
                .orElseThrow(() -> {
                    log.error("Doctor associated with appointment ID: {} not found. This indicates data inconsistency.", appointmentId);
                    return new UserNotFoundException("Doctor not found.");
                });
        
        LocalDateTime proposedAppointmentDateTime = LocalDateTime.of(
                appointmentUpdateDTO.getDateOfAppointment(),
                appointmentUpdateDTO.getTimeSlotRequestDTO().getStartTime()
            );
            if (proposedAppointmentDateTime.isBefore(LocalDateTime.now())) {
                log.warn("Attempt to book appointment for a past date/time: {}", proposedAppointmentDateTime);
                throw new InvalidAppointmentException("Cannot book an appointment for a date or time that has already passed.");
            }
 
        Availability selectedAvailability = doctor.getAvailabilities().stream()
                .filter(a -> a.getDate().equals(appointmentUpdateDTO.getDateOfAppointment()))
                .findFirst()
                .orElseThrow(() -> {
                    log.warn("No availability found for doctor ID: {} on new date: {}", doctor.getUserId(), appointmentUpdateDTO.getDateOfAppointment());
                    return new InvalidAppointmentException("No availability found for the selected date.");
                });
 
        TimeSlot oldTimeSlot = appointment.getTimeSlot();
        log.debug("Releasing old time slot: {} for appointment ID: {}", oldTimeSlot.getTimeSlotId(), appointmentId);
        oldTimeSlot.setAvailable(true);
        timeSlotRepo.save(oldTimeSlot);
 
        TimeSlot newTimeSlot = timeSlotRepo.findByStartTimeAndAvailabilityId(
                appointmentUpdateDTO.getTimeSlotRequestDTO().getStartTime(), selectedAvailability.getId())
                .orElseThrow(() -> {
                    log.warn("New time slot not found for start time: {} and availability ID: {}", appointmentUpdateDTO.getTimeSlotRequestDTO().getStartTime(), selectedAvailability.getId());
                    return new InvalidAppointmentException("New time slot not found.");
                });
 
        if (!newTimeSlot.isAvailable()) {
            log.warn("Selected new time slot for doctor ID: {} at {} on {} is not available.", doctor.getUserId(), appointmentUpdateDTO.getTimeSlotRequestDTO().getStartTime(), appointmentUpdateDTO.getDateOfAppointment());
            throw new TimeSlotUnavailableException("Selected new time slot is not available.");
        }
 
        log.debug("Assigning new time slot: {} to appointment ID: {}", newTimeSlot.getTimeSlotId(), appointmentId);
        newTimeSlot.setAvailable(false);
        timeSlotRepo.save(newTimeSlot);
        appointment.setTimeSlot(newTimeSlot);
        appointment.setDateOfAppointment(appointmentUpdateDTO.getDateOfAppointment());
        
        
        appointmentRepo.save(appointment);
        log.info("Appointment with ID: {} successfully updated to date: {} and time: {}", appointmentId, appointment.getDateOfAppointment(), appointment.getTimeSlot().getStartTime());
        notificationfeign.sendMailForAppointmentStatus( AppointmentMapper.convertToDto(appointment));
        log.info("Notification sent for updated appointment with ID: {}", appointment.getAppointmentId());
        return AppointmentMapper.convertToDto(appointment);
    }
 
    @Override
    public AppointmentResponseDTO cancelAppointment(Long appointmentId) {
        log.info("Attempting to cancel appointment with ID: {}", appointmentId);
        Appointment appointment = appointmentRepo.findById(appointmentId)
                .orElseThrow(() -> {
                    log.warn("Appointment not found with ID: {}", appointmentId);
                    return new InvalidAppointmentException("Appointment not found with ID: " + appointmentId);
                });
 
        appointment.setStatus(Status.CANCELLED);
        log.debug("Setting status to CANCELLED for appointment ID: {}", appointmentId);
 
        TimeSlot timeSlot = timeSlotRepo.findById(appointment.getTimeSlot().getTimeSlotId())
                .orElseThrow(() -> {
                    log.error("Time slot associated with appointment ID: {} not found. This indicates data inconsistency.", appointmentId);
                    return new InvalidAppointmentException("Time slot not found.");
                });
 
        log.debug("Marking time slot {} as available for appointment ID: {}", timeSlot.getTimeSlotId(), appointmentId);
        timeSlot.setAvailable(true);
        timeSlotRepo.save(timeSlot);
 
        appointmentRepo.save(appointment);
        log.info("Appointment with ID: {} successfully cancelled.", appointmentId);
        notificationfeign.sendMailForAppointmentStatus( AppointmentMapper.convertToDto(appointment));
        log.info("Notification sent for cancelled appointment with ID: {}", appointment.getAppointmentId());
        return AppointmentMapper.convertToDto(appointment);
    }
    
    @Override
    public AppointmentResponseDTO updateCompletion(Long appointmentId) {
    	log.info("Attempting to update completion status for appointment with ID: {}", appointmentId);
    	Appointment appointment = appointmentRepo.findById(appointmentId)
                .orElseThrow(() -> {
                    log.warn("Appointment not found with ID: {}", appointmentId);
                    return new InvalidAppointmentException("Appointment not found with ID: " +appointmentId);
                });
    	
    	LocalDateTime dateTime=LocalDateTime.of(appointment.getDateOfAppointment(), appointment.getTimeSlot().getEndTime());
        
    	
        if(LocalDateTime.now().isAfter(dateTime)) {
        	appointment.setStatus(Status.COMPLETED);
        	log.info("Appointment with ID: {} marked as COMPLETED because current time is after appointment end time.", appointmentId);
        } else {
            log.warn("Appointment with ID: {} cannot be marked as COMPLETED yet, as current time is before appointment end time.", appointmentId);
        }
        
        notificationfeign.sendMailForAppointmentStatus( AppointmentMapper.convertToDto(appointment));
        log.info("Notification sent for completion status update of appointment with ID: {}", appointment.getAppointmentId());
        
        return AppointmentMapper.convertToDto(appointment);
        
    }
    @Override
    public Long getPatientIdForAppointment(Long appointmentId) {
        log.debug("AppointmentService: Attempting to get patient ID for appointment ID: {}", appointmentId); // <<< THIS ONE
        return appointmentRepo.findById(appointmentId)
                .map(appointment -> {
                    if (appointment.getPatient() != null) {
                        log.debug("AppointmentService: Found appointment {} with patient ID: {}", appointmentId, appointment.getPatient().getUserId()); // <<< THIS ONE
                        return appointment.getPatient().getUserId();
                    } else {
                        log.warn("AppointmentService: Appointment {} found, but has no associated patient. Returning null.", appointmentId); // <<< THIS ONE
                        return null; // Ensure it returns null if no patient, or throw a specific exception.
                    }
                })
                .orElseThrow(() -> {
                    log.error("AppointmentService: Appointment not found with ID: {}", appointmentId); // <<< THIS ONE
                    return new InvalidAppointmentException("Appointment not found with ID: " + appointmentId);
                });
    }
}