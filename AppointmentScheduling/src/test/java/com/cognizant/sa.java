package com.cognizant;

import com.cognizant.dto.AppointmentRequestDto;
import com.cognizant.dto.AppointmentResponseDTO;
import com.cognizant.dto.AppointmentUpdateDTO;
import com.cognizant.dto.TimeSlotRequestDTO;
import com.cognizant.entity.*;
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
import com.cognizant.services.AppointmentService;
import com.cognizant.services.Notificationfeign;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceWithoutBuilderTest { // Renamed class to reflect the change

    @Mock
    private AppointmentRepository appointmentRepo;

    @Mock
    private UserRepository userRepo;

    @Mock
    private TimeSlotRepository timeSlotRepo;

    @Mock
    private Notificationfeign notificationfeign;

    @InjectMocks
    private AppointmentService appointmentService;

    private User doctor;
    private User patient;
    private Availability doctorAvailability;
    private TimeSlot availableTimeSlot;
    private TimeSlot bookedTimeSlot;
    private Appointment appointment;
    private AppointmentRequestDto appointmentRequestDto;
    private AppointmentUpdateDTO appointmentUpdateDTO;

    @BeforeEach
    void setUp() {
        // --- Creating User objects without builder ---
        doctor = new User();
        doctor.setUserId(1L);
        doctor.setEmail("doctor@example.com");
        doctor.setRole(Role.DOCTOR);

        patient = new User();
        patient.setUserId(2L);
        patient.setEmail("patient@example.com");
        patient.setRole(Role.PATIENT);

        // --- Creating Availability object without builder ---
        doctorAvailability = new Availability();
        doctorAvailability.setId(10L);
        doctorAvailability.setDoctor(doctor);
        doctorAvailability.setDate(LocalDate.of(2025, 6, 15));
        doctor.setAvailabilities(Collections.singletonList(doctorAvailability));

        // --- Creating TimeSlot objects without builder ---
        availableTimeSlot = new TimeSlot();
        availableTimeSlot.setTimeSlotId(100L);
        availableTimeSlot.setAvailability(doctorAvailability);
        availableTimeSlot.setStartTime(LocalTime.of(10, 0));
        availableTimeSlot.setEndTime(LocalTime.of(10, 30));
        availableTimeSlot.setAvailable(true);

        bookedTimeSlot = new TimeSlot();
        bookedTimeSlot.setTimeSlotId(101L);
        bookedTimeSlot.setAvailability(doctorAvailability);
        bookedTimeSlot.setStartTime(LocalTime.of(11, 0));
        bookedTimeSlot.setEndTime(LocalTime.of(11, 30));
        bookedTimeSlot.setAvailable(false);

        // --- Creating Appointment object without builder ---
        appointment = new Appointment();
        appointment.setAppointmentId(1L);
        appointment.setDoctor(doctor);
        appointment.setPatient(patient);
        appointment.setDateOfAppointment(LocalDate.of(2025, 6, 15));
        appointment.setTimeSlot(availableTimeSlot);
        appointment.setStatus(Status.BOOKED);
        availableTimeSlot.setAppointment(appointment);

        // --- Creating DTOs without builder ---
        TimeSlotRequestDTO requestTimeSlotDto = new TimeSlotRequestDTO();
        requestTimeSlotDto.setStartTime(LocalTime.of(10, 0));
        requestTimeSlotDto.setEndTime(LocalTime.of(10, 30));

        appointmentRequestDto = new AppointmentRequestDto();
        appointmentRequestDto.setDoctorId(1L);
        appointmentRequestDto.setPatientId(2L);
        appointmentRequestDto.setDateOfAppointment(LocalDate.of(2025, 6, 15));
        appointmentRequestDto.setTimeSlot(requestTimeSlotDto);

        TimeSlotRequestDTO updateTimeSlotDto = new TimeSlotRequestDTO();
        updateTimeSlotDto.setStartTime(LocalTime.of(14, 0));
        updateTimeSlotDto.setEndTime(LocalTime.of(14, 30));

        appointmentUpdateDTO = new AppointmentUpdateDTO();
        appointmentUpdateDTO.setDateOfAppointment(LocalDate.of(2025, 6, 16));
        appointmentUpdateDTO.setTimeSlotRequestDTO(updateTimeSlotDto);
    }

    // --- All your existing test cases will remain the same ---
    // For brevity, I'm omitting them here, but they would be identical
    // to your original test class after the setUp() method.

    // --- viewAll Test Cases ---
    @Test
    void viewAll_shouldReturnListOfAppointments() {
        when(appointmentRepo.findAll()).thenReturn(Arrays.asList(appointment));
        List<AppointmentResponseDTO> result = appointmentService.viewAll();
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        verify(appointmentRepo, times(1)).findAll();
    }

    // --- viewByAppointmentId Test Cases ---
    @Test
    void viewByAppointmentId_shouldReturnAppointment_whenFound() {
        when(appointmentRepo.findById(1L)).thenReturn(Optional.of(appointment));
        AppointmentResponseDTO result = appointmentService.viewByAppointmentId(1L);
        assertNotNull(result);
        assertEquals(appointment.getAppointmentId(), result.getAppointmentId());
        verify(appointmentRepo, times(1)).findById(1L);
    }

    @Test
    void viewByAppointmentId_shouldThrowInvalidAppointmentException_whenNotFound() {
        when(appointmentRepo.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(InvalidAppointmentException.class, () -> appointmentService.viewByAppointmentId(99L));
        verify(appointmentRepo, times(1)).findById(99L);
    }

    // --- addAppointment Test Cases ---
    @Test
    void addAppointment_shouldSuccessfullyBookAppointment() {
        // Arrange
        when(userRepo.findById(doctor.getUserId())).thenReturn(Optional.of(doctor));
        when(userRepo.findById(patient.getUserId())).thenReturn(Optional.of(patient));
        when(timeSlotRepo.findByStartTimeAndAvailabilityId(
                appointmentRequestDto.getTimeSlot().getStartTime(), doctorAvailability.getId()))
                .thenReturn(Optional.of(availableTimeSlot));
        when(appointmentRepo.save(any(Appointment.class))).thenReturn(appointment);
        doNothing().when(notificationfeign).sendMailForAppointmentStatus(any(AppointmentResponseDTO.class));

        // Act
        AppointmentResponseDTO result = appointmentService.addAppointment(appointmentRequestDto);

        // Assert
        assertNotNull(result);
        assertEquals(Status.BOOKED, result.getStatus());
        assertFalse(availableTimeSlot.isAvailable()); // Verify time slot is marked unavailable
        verify(appointmentRepo, times(1)).save(any(Appointment.class));
        verify(notificationfeign, times(1)).sendMailForAppointmentStatus(any(AppointmentResponseDTO.class));
    }

    @Test
    void addAppointment_shouldThrowUserNotFoundException_whenDoctorNotFound() {
        when(userRepo.findById(doctor.getUserId())).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> appointmentService.addAppointment(appointmentRequestDto));
        verify(userRepo, times(1)).findById(doctor.getUserId());
        verify(appointmentRepo, never()).save(any(Appointment.class)); // Ensure no save operation occurs
    }

    @Test
    void addAppointment_shouldThrowInvalidAppointmentException_whenDoctorRoleIsIncorrect() {
        User nonDoctor = new User(); // No builder
        nonDoctor.setUserId(1L);
        nonDoctor.setRole(Role.PATIENT);

        when(userRepo.findById(doctor.getUserId())).thenReturn(Optional.of(nonDoctor));
        when(userRepo.findById(patient.getUserId())).thenReturn(Optional.of(patient)); // Ensure patient is found
        assertThrows(InvalidAppointmentException.class, () -> appointmentService.addAppointment(appointmentRequestDto));
        verify(userRepo, times(1)).findById(doctor.getUserId());
        verify(appointmentRepo, never()).save(any(Appointment.class));
    }

    @Test
    void addAppointment_shouldThrowTimeSlotUnavailableException_whenSlotNotAvailable() {
        // Arrange: Mock the specific time slot to be *not* available
        when(userRepo.findById(doctor.getUserId())).thenReturn(Optional.of(doctor));
        when(userRepo.findById(patient.getUserId())).thenReturn(Optional.of(patient));
        when(timeSlotRepo.findByStartTimeAndAvailabilityId(
                appointmentRequestDto.getTimeSlot().getStartTime(), doctorAvailability.getId()))
                .thenReturn(Optional.of(bookedTimeSlot)); // Return a booked slot

        // Act & Assert
        assertThrows(TimeSlotUnavailableException.class, () -> appointmentService.addAppointment(appointmentRequestDto));
        verify(appointmentRepo, never()).save(any(Appointment.class));
    }

    // --- updateAppointment Test Cases ---
    @Test
    void updateAppointment_shouldSuccessfullyUpdateAppointment() {
        // Arrange
        // Assume original appointment has its slot taken
        appointment.setTimeSlot(bookedTimeSlot); // Assume the original appointment had this slot
        bookedTimeSlot.setAvailable(false); // Make sure it's set to unavailable initially for test setup

        // The new slot for the update
        TimeSlot newAvailableTimeSlot = new TimeSlot(); // No builder
        newAvailableTimeSlot.setTimeSlotId(200L);
        newAvailableTimeSlot.setAvailability(doctorAvailability);
        newAvailableTimeSlot.setStartTime(LocalTime.of(14, 0));
        newAvailableTimeSlot.setEndTime(LocalTime.of(14, 30));
        newAvailableTimeSlot.setAvailable(true);

        // Ensure doctor has availability for the new date
        Availability newDoctorAvailability = new Availability(); // No builder
        newDoctorAvailability.setId(11L);
        newDoctorAvailability.setDoctor(doctor);
        newDoctorAvailability.setDate(LocalDate.of(2025, 6, 16));
        doctor.setAvailabilities(Arrays.asList(doctorAvailability, newDoctorAvailability));


        when(appointmentRepo.findById(appointment.getAppointmentId())).thenReturn(Optional.of(appointment));
        when(userRepo.findById(doctor.getUserId())).thenReturn(Optional.of(doctor));
        when(timeSlotRepo.save(bookedTimeSlot)).thenReturn(bookedTimeSlot); // Old slot released
        when(timeSlotRepo.findByStartTimeAndAvailabilityId(
                appointmentUpdateDTO.getTimeSlotRequestDTO().getStartTime(), newDoctorAvailability.getId()))
                .thenReturn(Optional.of(newAvailableTimeSlot)); // New slot found
        when(timeSlotRepo.save(newAvailableTimeSlot)).thenReturn(newAvailableTimeSlot); // New slot booked
        when(appointmentRepo.save(any(Appointment.class))).thenReturn(appointment);
        doNothing().when(notificationfeign).sendMailForAppointmentStatus(any(AppointmentResponseDTO.class));

        // Act
        AppointmentResponseDTO result = appointmentService.updateAppointment(appointment.getAppointmentId(), appointmentUpdateDTO);

        // Assert
        assertNotNull(result);
        assertEquals(appointmentUpdateDTO.getDateOfAppointment(), result.getDateOfAppointment());
        assertEquals(appointmentUpdateDTO.getTimeSlotRequestDTO().getStartTime(), result.getTimeSlot().getStartTime());
        assertTrue(bookedTimeSlot.isAvailable()); // Old slot should be released
        assertFalse(newAvailableTimeSlot.isAvailable()); // New slot should be booked
        verify(appointmentRepo, times(1)).save(any(Appointment.class));
        verify(notificationfeign, times(1)).sendMailForAppointmentStatus(any(AppointmentResponseDTO.class));
    }

    @Test
    void updateAppointment_shouldThrowInvalidAppointmentException_whenAppointmentNotFound() {
        when(appointmentRepo.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(InvalidAppointmentException.class, () -> appointmentService.updateAppointment(99L, appointmentUpdateDTO));
        verify(appointmentRepo, times(1)).findById(99L);
    }

    @Test
    void updateAppointment_shouldThrowInvalidAppointmentException_whenNewAvailabilityNotFound() {
        // Arrange: Doctor does not have availability for the updated date
        doctor.setAvailabilities(Collections.singletonList(doctorAvailability)); // Only current date availability
        when(appointmentRepo.findById(appointment.getAppointmentId())).thenReturn(Optional.of(appointment));
        when(userRepo.findById(doctor.getUserId())).thenReturn(Optional.of(doctor));

        // Act & Assert
        assertThrows(InvalidAppointmentException.class, () -> appointmentService.updateAppointment(appointment.getAppointmentId(), appointmentUpdateDTO));
        verify(timeSlotRepo, never()).save(any(TimeSlot.class)); // No time slot operations should occur
        verify(appointmentRepo, never()).save(any(Appointment.class));
    }


    // --- cancelAppointment Test Cases ---
    @Test
    void cancelAppointment_shouldSuccessfullyCancelAppointment() {
        // Arrange
        // Assume the appointment's time slot is currently unavailable
        availableTimeSlot.setAvailable(false);
        appointment.setTimeSlot(availableTimeSlot); // Ensure the time slot is linked to the appointment

        when(appointmentRepo.findById(appointment.getAppointmentId())).thenReturn(Optional.of(appointment));
        when(timeSlotRepo.findById(appointment.getTimeSlot().getTimeSlotId())).thenReturn(Optional.of(availableTimeSlot));
        when(appointmentRepo.save(any(Appointment.class))).thenReturn(appointment);
        when(timeSlotRepo.save(any(TimeSlot.class))).thenReturn(availableTimeSlot);
        doNothing().when(notificationfeign).sendMailForAppointmentStatus(any(AppointmentResponseDTO.class));

        // Act
        AppointmentResponseDTO result = appointmentService.cancelAppointment(appointment.getAppointmentId());

        // Assert
        assertNotNull(result);
        assertEquals(Status.CANCELLED, result.getStatus());
        assertTrue(availableTimeSlot.isAvailable()); // Time slot should be marked available
        verify(appointmentRepo, times(1)).save(appointment);
        verify(notificationfeign, times(1)).sendMailForAppointmentStatus(any(AppointmentResponseDTO.class));
    }

    @Test
    void cancelAppointment_shouldThrowInvalidAppointmentException_whenAppointmentNotFound() {
        when(appointmentRepo.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(InvalidAppointmentException.class, () -> appointmentService.cancelAppointment(99L));
        verify(appointmentRepo, times(1)).findById(99L);
    }

    // --- updateCompletion Test Cases ---
    @Test
    void updateCompletion_shouldMarkAppointmentCompleted_whenCurrentTimeIsAfterEndTime() {
        // Arrange: Set appointment time to be in the past
        appointment.setDateOfAppointment(LocalDate.now().minusDays(1));
        appointment.getTimeSlot().setEndTime(LocalTime.now().minusHours(1));

        when(appointmentRepo.findById(appointment.getAppointmentId())).thenReturn(Optional.of(appointment));
        when(appointmentRepo.save(any(Appointment.class))).thenReturn(appointment);
        doNothing().when(notificationfeign).sendMailForAppointmentStatus(any(AppointmentResponseDTO.class));

        // Act
        AppointmentResponseDTO result = appointmentService.updateCompletion(appointment.getAppointmentId());

        // Assert
        assertNotNull(result);
        assertEquals(Status.COMPLETED, result.getStatus());
        verify(appointmentRepo, times(1)).save(appointment);
        verify(notificationfeign, times(1)).sendMailForAppointmentStatus(any(AppointmentResponseDTO.class));
    }

    @Test
    void updateCompletion_shouldNotMarkAppointmentCompleted_whenCurrentTimeIsBeforeEndTime() {
        // Arrange: Set appointment time to be in the future
        appointment.setDateOfAppointment(LocalDate.now().plusDays(1));
        appointment.getTimeSlot().setEndTime(LocalTime.of(10, 0)); // Future time
        appointment.setStatus(Status.BOOKED); // Ensure it's not already completed

        when(appointmentRepo.findById(appointment.getAppointmentId())).thenReturn(Optional.of(appointment));
        doNothing().when(notificationfeign).sendMailForAppointmentStatus(any(AppointmentResponseDTO.class)); // Notification always sent

        // Act
        AppointmentResponseDTO result = appointmentService.updateCompletion(appointment.getAppointmentId());

        // Assert
        assertNotNull(result);
        assertEquals(Status.BOOKED, result.getStatus()); // Status should remain unchanged
        verify(appointmentRepo, never()).save(any(Appointment.class)); // Save should not be called as status doesn't change
        verify(notificationfeign, times(1)).sendMailForAppointmentStatus(any(AppointmentResponseDTO.class));
    }

    @Test
    void updateCompletion_shouldThrowInvalidAppointmentException_whenAppointmentNotFound() {
        when(appointmentRepo.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(InvalidAppointmentException.class, () -> appointmentService.updateCompletion(99L));
        verify(appointmentRepo, times(1)).findById(1L);
    }
}