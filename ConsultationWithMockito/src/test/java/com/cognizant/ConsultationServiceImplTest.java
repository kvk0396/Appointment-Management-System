package com.cognizant;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.cognizant.config.AppointmentClient;
import com.cognizant.dto.ConsultationResponseDTO;
import com.cognizant.entity.Appointment;
import com.cognizant.entity.Consultation;
import com.cognizant.exceptions.InvalidAppointmentIdException;
import com.cognizant.exceptions.InvalidConsultationIdException;
import com.cognizant.repository.AppointmentRepository;
import com.cognizant.repository.ConsultationRepository;
import com.cognizant.service.ConsultationServiceImpl;

@ExtendWith(MockitoExtension.class)
public class ConsultationServiceImplTest {

    @Mock
    private ConsultationRepository consultationRepository;

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private AppointmentClient appointmentClient;

    @InjectMocks
    private ConsultationServiceImpl consultationService;

    private Consultation consultation;
    private Appointment appointment;
    private ConsultationResponseDTO consultationResponseDTO;

    @BeforeEach
    void setUp() {
        // Initialize common objects for tests
        appointment = new Appointment();
        appointment.setAppointmentId(1L);
        // Set other appointment properties if needed

        consultation = new Consultation();
        consultation.setConsultationId(101L);
        consultation.setNotes("Patient has a common cold.");
        consultation.setPrescription("Rest and fluids.");
        consultation.setAppointment(appointment);

        consultationResponseDTO = new ConsultationResponseDTO();
        consultationResponseDTO.setConsultationId(101L);
        consultationResponseDTO.setAppointmentId(1L);
        consultationResponseDTO.setNotes("Patient has a common cold.");
        consultationResponseDTO.setPrescription("Rest and fluids.");
    }

    @Test
    void testCreateConsultation_Success() {
        // Mock behavior for appointmentRepository
        when(appointmentRepository.findById(anyLong())).thenReturn(Optional.of(appointment));
        // Mock behavior for consultationRepository.save
        when(consultationRepository.save(any(Consultation.class))).thenReturn(consultation);
        // Mock behavior for appointmentClient (void method)
        doNothing().when(appointmentClient).updateAppointment(anyLong());

        ConsultationResponseDTO result = consultationService.createConsultation(consultationResponseDTO);

        assertNotNull(result);
        assertEquals(consultation.getConsultationId(), result.getConsultationId());
        assertEquals(consultation.getNotes(), result.getNotes());
        assertEquals(consultation.getPrescription(), result.getPrescription());
        assertEquals(consultation.getAppointment().getAppointmentId(), result.getAppointmentId());

        // Verify that the methods were called
        verify(appointmentRepository, times(1)).findById(anyLong());
        verify(consultationRepository, times(1)).save(any(Consultation.class));
        verify(appointmentClient, times(1)).updateAppointment(anyLong());
    }

    @Test
    void testCreateConsultation_InvalidAppointmentId() {
        // Mock behavior to simulate appointment not found
        when(appointmentRepository.findById(anyLong())).thenReturn(Optional.empty());

        InvalidAppointmentIdException exception = assertThrows(InvalidAppointmentIdException.class, () -> {
            consultationService.createConsultation(consultationResponseDTO);
        });

        assertEquals("Appointment not found with ID: " + consultationResponseDTO.getAppointmentId(), exception.getMessage());
        // Verify that save and updateAppointment were never called
        verify(consultationRepository, never()).save(any(Consultation.class));
        verify(appointmentClient, never()).updateAppointment(anyLong());
    }

    @Test
    void testGetConsultationDTOById_Success() {
        // Mock behavior for consultationRepository.findById
        when(consultationRepository.findById(anyLong())).thenReturn(Optional.of(consultation));

        ConsultationResponseDTO result = consultationService.getConsultationDTOById(101L);

        assertNotNull(result);
        assertEquals(consultation.getConsultationId(), result.getConsultationId());
        assertEquals(consultation.getNotes(), result.getNotes());
        assertEquals(consultation.getPrescription(), result.getPrescription());
        assertEquals(consultation.getAppointment().getAppointmentId(), result.getAppointmentId());

        verify(consultationRepository, times(1)).findById(anyLong());
    }

    @Test
    void testGetConsultationDTOById_InvalidConsultationId() {
        // Mock behavior to simulate consultation not found
        when(consultationRepository.findById(anyLong())).thenReturn(Optional.empty());

        InvalidConsultationIdException exception = assertThrows(InvalidConsultationIdException.class, () -> {
            consultationService.getConsultationDTOById(999L);
        });

        assertEquals("Consultation not found with ID: " + 999L, exception.getMessage());
        verify(consultationRepository, times(1)).findById(anyLong());
    }

    @Test
    void testUpdateConsultation_Success() {
        // Create a DTO with updated information
        ConsultationResponseDTO updatedDto = new ConsultationResponseDTO();
        updatedDto.setNotes("Patient is recovering well.");
        updatedDto.setPrescription("Continue previous medication.");

        // Mock behavior for consultationRepository.findById
        when(consultationRepository.findById(anyLong())).thenReturn(Optional.of(consultation));
        // Mock behavior for consultationRepository.save
        when(consultationRepository.save(any(Consultation.class))).thenReturn(consultation); // Returning the same object, but with updated fields

        ConsultationResponseDTO result = consultationService.updateConsultation(101L, updatedDto);

        assertNotNull(result);
        assertEquals(consultation.getConsultationId(), result.getConsultationId());
        assertEquals(updatedDto.getNotes(), result.getNotes()); // Assert with updated notes
        assertEquals(updatedDto.getPrescription(), result.getPrescription()); // Assert with updated prescription

        verify(consultationRepository, times(1)).findById(anyLong());
        verify(consultationRepository, times(1)).save(any(Consultation.class));
    }

    @Test
    void testUpdateConsultation_InvalidConsultationId() {
        // Mock behavior to simulate consultation not found
        when(consultationRepository.findById(anyLong())).thenReturn(Optional.empty());

        ConsultationResponseDTO updatedDto = new ConsultationResponseDTO(); // DTO for update

        InvalidConsultationIdException exception = assertThrows(InvalidConsultationIdException.class, () -> {
            consultationService.updateConsultation(999L, updatedDto);
        });

        assertEquals("Consultation not found with ID: " + 999L, exception.getMessage());
        verify(consultationRepository, times(1)).findById(anyLong());
        verify(consultationRepository, never()).save(any(Consultation.class)); // Verify save was not called
    }

    @Test
    void testGetConsultationsByAppointmentId_Success() {
        // Prepare a list of consultations
        List<Consultation> consultations = new ArrayList<>();
        consultations.add(consultation); // Add the common consultation
        
        Consultation anotherConsultation = new Consultation();
        anotherConsultation.setConsultationId(102L);
        anotherConsultation.setNotes("Follow-up required.");
        anotherConsultation.setPrescription("New medication.");
        anotherConsultation.setAppointment(appointment);
        consultations.add(anotherConsultation);

        // Mock behavior for appointmentRepository.findById
        when(appointmentRepository.findById(anyLong())).thenReturn(Optional.of(appointment));
        // Mock behavior for consultationRepository.findByAppointmentAppointmentId
        when(consultationRepository.findByAppointmentAppointmentId(anyLong())).thenReturn(consultations);

        List<ConsultationResponseDTO> result = consultationService.getConsultationsByAppointmentId(1L);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(2, result.size());
        assertEquals(101L, result.get(0).getConsultationId());
        assertEquals(102L, result.get(1).getConsultationId());

        verify(appointmentRepository, times(1)).findById(anyLong());
        verify(consultationRepository, times(1)).findByAppointmentAppointmentId(anyLong());
    }

    @Test
    void testGetConsultationsByAppointmentId_InvalidAppointmentId() {
        // Mock behavior to simulate appointment not found
        when(appointmentRepository.findById(anyLong())).thenReturn(Optional.empty());

        InvalidAppointmentIdException exception = assertThrows(InvalidAppointmentIdException.class, () -> {
            consultationService.getConsultationsByAppointmentId(999L);
        });

        assertEquals("Appointment not found with ID: " + 999L, exception.getMessage());
        verify(appointmentRepository, times(1)).findById(anyLong());
        verify(consultationRepository, never()).findByAppointmentAppointmentId(anyLong()); // Verify it wasn't called
    }

    @Test
    void testGetConsultationsByAppointmentId_NoConsultationsFound() {
        // Mock behavior for appointmentRepository.findById
        when(appointmentRepository.findById(anyLong())).thenReturn(Optional.of(appointment));
        // Mock behavior for consultationRepository.findByAppointmentAppointmentId to return an empty list
        when(consultationRepository.findByAppointmentAppointmentId(anyLong())).thenReturn(new ArrayList<>());

        List<ConsultationResponseDTO> result = consultationService.getConsultationsByAppointmentId(1L);

        assertNotNull(result);
        assertTrue(result.isEmpty()); // Expect an empty list

        verify(appointmentRepository, times(1)).findById(anyLong());
        verify(consultationRepository, times(1)).findByAppointmentAppointmentId(anyLong());
    }
}