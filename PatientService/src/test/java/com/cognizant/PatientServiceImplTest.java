package com.cognizant;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.*;

import java.time.LocalDateTime;

import java.util.Collections;

import java.util.List;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;

import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;

import org.mockito.Mock;

import org.mockito.MockitoAnnotations;

import org.mockito.junit.jupiter.MockitoExtension;

import com.cognizant.config.APIClient;

import com.cognizant.dto.AppointmentDTO;

import com.cognizant.dto.UserDTO;

import com.cognizant.entity.Appointment;

import com.cognizant.entity.User;

import com.cognizant.enums.Role;

import com.cognizant.exceptions.RoleMismatchException;

import com.cognizant.exceptions.UserNotFoundException;

import com.cognizant.repository.AppointmentRepository;

import com.cognizant.repository.UserRepository;

import com.cognizant.service.PatientServiceImpl;

@ExtendWith(MockitoExtension.class)

public class PatientServiceImplTest {

    @InjectMocks

    private PatientServiceImpl patientService;

    @Mock

    private UserRepository userRepository;

    @Mock

    private APIClient apiClient;

    @Mock

    private AppointmentRepository appointmentRepository;

    private UserDTO userDTO;

    private User user;

    @BeforeEach

    void setUp() {

        MockitoAnnotations.openMocks(this);

        userDTO = new UserDTO();

        userDTO.setUserId(1L);

        userDTO.setEmail("patient@example.com");

        userDTO.setName("John Doe");

        userDTO.setPhone("1234567890");
        
        userDTO.setRole(Role.PATIENT);

        user = new User();

        user.setUserId(1L);

        user.setEmail("patient@example.com");

        user.setName("John Doe");

        user.setPhone("1234567890");

        user.setRole(Role.PATIENT);

    }

    @Test

    void testAddUser_Success() {

        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDTO result = patientService.addUser(userDTO);

        assertNotNull(result);

        assertEquals(userDTO.getEmail(), result.getEmail());

        verify(apiClient).sendMailForUserRegistry(any(UserDTO.class));

    }

    @Test

    void testUpdateUser_Success() {

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDTO updatedDTO = new UserDTO();

        updatedDTO.setUserId(1L);

        updatedDTO.setEmail("updated@example.com");

        updatedDTO.setName("Updated Name");

        updatedDTO.setPhone("9876543210");
        
        updatedDTO.setRole(Role.PATIENT);
        UserDTO result = patientService.updateUser(1L, updatedDTO);

        assertNotNull(result);

        assertEquals("updated@example.com", result.getEmail());

    }

    @Test

    void testViewAllUsers_Success() {

        List<User> users = List.of(user);

        when(userRepository.findByRole(Role.PATIENT)).thenReturn(Optional.of(users));

        List<UserDTO> result = patientService.viewAllUsers();

        assertNotNull(result);

        assertEquals(1, result.size());

    }

    @Test

    void testViewPatientById_Success() {

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserDTO result = patientService.viewPatientById(1L);

        assertNotNull(result);

        assertEquals("patient@example.com", result.getEmail());

    }

    @Test

    void testViewAllPatientAppointments_Success() {

        Appointment appointment = new Appointment();

        appointment.setAppointmentId(1L);

        

        user.setPatientAppointments(List.of(appointment));

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        List<AppointmentDTO> result = patientService.viewAllPatientAppointments(1L);

        assertNotNull(result);

        assertEquals(1, result.size());

    }

    @Test

    void testViewAllPatientAppointments_RoleMismatch() {

        user.setRole(Role.DOCTOR);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        assertThrows(RoleMismatchException.class, () -> patientService.viewAllPatientAppointments(1L));

    }

    

    @Test

    void testUpdateUser_WhenUserNotFound() {

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> patientService.updateUser(1L, userDTO));

    }

    

    @Test

    void testViewPatientById_WhenUserNotFound() {

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> patientService.viewPatientById(1L));

    }

}