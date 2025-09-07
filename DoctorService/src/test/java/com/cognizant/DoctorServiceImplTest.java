//package com.cognizant;
//
//import static org.hamcrest.CoreMatchers.any;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.Optional;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import com.cognizant.config.APIClient;
//import com.cognizant.dto.AppointmentDTO;
//import com.cognizant.dto.UserDTO;
//import com.cognizant.entity.Appointment;
//import com.cognizant.entity.User;
//import com.cognizant.enums.Role;
//import com.cognizant.exceptions.RoleMismatchException;
//import com.cognizant.repository.UserRepository;
//import com.cognizant.service.DoctorServiceImpl;
//
//@ExtendWith(MockitoExtension.class)
//public class DoctorServiceImplTest {
//
//    @InjectMocks
//    private DoctorServiceImpl doctorService;
//
//    @Mock
//    private UserRepository userRepository;
//
//    @Mock
//    private APIClient apiClient;
//
//    private UserDTO userDTO;
//    private User user;
//
//    @BeforeEach
//    void setUp() {
//        userDTO = new UserDTO();
//        userDTO.setUserId(1L);
//        userDTO.setEmail("doctor@example.com");
//        userDTO.setName("Dr. Smith");
//        userDTO.setPhone("1234567890");
//        userDTO.setRole(Role.DOCTOR);
//
//        user = new User();
//        user.setUserId(1L);
//        user.setEmail("doctor@example.com");
//        user.setName("Dr. Smith");
//        user.setPhone("1234567890");
//        user.setRole(Role.DOCTOR);
//    }
//
//    @Test
//    void testAddUser_Success() {
//        when(userRepository.save(any(User.class))).thenReturn(user);
//
//        UserDTO result = doctorService.addUser(userDTO);
//
//        assertNotNull(result);
//        assertEquals(userDTO.getEmail(), result.getEmail());
//        verify(apiClient).sendMailForUserRegistry(any(UserDTO.class));
//    }
//
//    @Test
//    void testUpdateUser_Success() {
//        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
//        when(userRepository.save(any(User.class))).thenReturn(user);
//
//        UserDTO updatedDTO = new UserDTO();
//        updatedDTO.setUserId(1L);
//        updatedDTO.setEmail("updated@example.com");
//        updatedDTO.setName("Updated Name");
//        updatedDTO.setPhone("9876543210");
//        updatedDTO.setRole(Role.DOCTOR);
//
//        UserDTO result = doctorService.updateUser(1L, updatedDTO);
//
//        assertNotNull(result);
//        assertEquals("updated@example.com", result.getEmail());
//    }
//
//    @Test
//    void testViewAllUsers_Success() {
//        List<User> users = List.of(user);
//        when(userRepository.findByRole(Role.DOCTOR)).thenReturn(Optional.of(users));
//
//        List<UserDTO> result = doctorService.viewAllUsers();
//
//        assertNotNull(result);
//        assertEquals(1, result.size());
//    }
//
//    @Test
//    void testViewDoctorById_Success() {
//        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
//
//        UserDTO result = doctorService.viewDoctorById(1L);
//
//        assertNotNull(result);
//        assertEquals("doctor@example.com", result.getEmail());
//    }
//
//    @Test
//    void testViewAllDoctorAppointments_Success() {
//        Appointment appointment = new Appointment();
//        appointment.setAppointmentId(1L);
//        appointment.setReason("General Checkup");
//        appointment.setDateTime(LocalDateTime.now());
//
//        user.setDoctorAppointments(List.of(appointment));
//        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
//
//        List<AppointmentDTO> result = doctorService.viewAllDoctorAppointments(1L);
//
//        assertNotNull(result);
//        assertEquals(1, result.size());
//    }
//
//    @Test
//    void testViewAllDoctorAppointments_RoleMismatch() {
//        user.setRole(Role.PATIENT);
//        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
//
//        assertThrows(RoleMismatchException.class, () -> doctorService.viewAllDoctorAppointments(1L));
//    }
//}
