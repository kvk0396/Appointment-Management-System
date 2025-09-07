package com.cognizant.service;

import com.cognizant.config.AppointmentClient;
import com.cognizant.dto.AppointmentResponseDTO;
import com.cognizant.dto.ConsultationResponseDTO;
import com.cognizant.entity.Appointment;
import com.cognizant.entity.Consultation;
import com.cognizant.exceptions.InvalidAppointmentIdException; // Ensure this import is correct
import com.cognizant.exceptions.InvalidConsultationIdException;
import com.cognizant.repository.AppointmentRepository;
import com.cognizant.repository.ConsultationRepository;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Added transactional for methods that modify data

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional; // Added import for Optional

@Service
public class ConsultationServiceImpl implements IConsultationService {

	@Autowired
	private ConsultationRepository consultationRepository;

	@Autowired
	private AppointmentRepository appointmentRepository;

	@Autowired
	private AppointmentClient appointmentClient;
	
	/**
	 * Creates a new consultation record.
	 *
	 * @param consultationDTO DTO containing consultation details.
	 * @return The created ConsultationResponseDTO.
	 * @throws InvalidAppointmentIdException If the associated appointment is not
	 *                                       found.
	 */
	@Transactional
	public ConsultationResponseDTO createConsultation(ConsultationResponseDTO consultationDTO) {
		Long appointmentId=consultationDTO.getAppointmentId();
		Appointment appointment = appointmentRepository.findById(appointmentId)
				.orElseThrow(() -> new InvalidAppointmentIdException(
						"Appointment not found with ID: " + consultationDTO.getAppointmentId()));

		Consultation consultation = new Consultation();
		consultation.setAppointment(appointment);
		// Ensure your Consultation entity has 'notes' and 'prescription' fields
		// based on your DTO, not 'diagnosis' and 'consultationDate' if they don't
		// exist.
		// Assuming 'notes' field now corresponds to the DTO's 'notes'
		consultation.setNotes(consultationDTO.getNotes());
		consultation.setPrescription(consultationDTO.getPrescription());
		// If your entity has a consultationDate, ensure it's handled.
		// Based on previous snippets, it seems it was removed/changed to just 'notes'.
		// If it exists in the entity and DTO, uncomment:
		// consultation.setConsultationDate(LocalDateTime.now());

		Consultation savedConsultation = consultationRepository.save(consultation);

		appointmentClient.updateAppointment(appointmentId);
		
		return mapToDTO(savedConsultation);
	}

	/**
	 * Retrieves a consultation by its ID. This method is named
	 * `getConsultationDTOById` as requested for the controller.
	 *
	 * @param consultationId  The ID of the consultation.
	 * @param consultationDTO
	 * @return An Optional containing the ConsultationResponseDTO if found.
	 */
	public ConsultationResponseDTO getConsultationDTOById(Long consultationId) {
		Consultation consultation = consultationRepository.findById(consultationId).orElseThrow(
				() -> new InvalidConsultationIdException("Consultation not found with ID: " + consultationId));

		return mapToDTO(consultation);
	}

	/**
	 * Updates an existing consultation record.
	 *
	 * @param consultationId  The ID of the consultation to update.
	 * @param consultationDTO DTO containing updated consultation details.
	 * @return The updated ConsultationResponseDTO.
	 * @throws ConsultationNotFoundException If the consultation to update is not
	 *                                       found.
	 */
	@Transactional
	public ConsultationResponseDTO updateConsultation(Long consultationId, ConsultationResponseDTO consultationDTO) {
		Consultation consultation = consultationRepository.findById(consultationId).orElseThrow(
				() -> new InvalidConsultationIdException("Consultation not found with ID: " + consultationId));

		consultation.setNotes(consultationDTO.getNotes());
		consultation.setPrescription(consultationDTO.getPrescription());

		Consultation updatedConsultation = consultationRepository.save(consultation);

		return mapToDTO(updatedConsultation);
	}

	/**
	 * Helper method to convert a Consultation entity to a ConsultationResponseDTO.
	 * Ensure the fields match your DTO and Entity (e.g., notes vs diagnosis).
	 *
	 * @param consultation The Consultation entity.
	 * @return The corresponding ConsultationResponseDTO.
	 */
	private ConsultationResponseDTO mapToDTO(Consultation consultation) {
		ConsultationResponseDTO dto = new ConsultationResponseDTO();
		dto.setConsultationId(consultation.getConsultationId()); // Assuming Consultation entity has getConsultationId()
		if (consultation.getAppointment() != null) {
			dto.setAppointmentId(consultation.getAppointment().getAppointmentId()); // Assuming Appointment entity has
																					// getAppointmentId()
		}
		dto.setNotes(consultation.getNotes());
		dto.setPrescription(consultation.getPrescription());
		// If your DTO also has consultationDate, uncomment and map it:
		// dto.setConsultationDate(consultation.getConsultationDate());
		return dto;
	}

	public List<ConsultationResponseDTO> getConsultationsByAppointmentId(Long appointmentId) {
			
			appointmentRepository.findById(appointmentId).orElseThrow(() -> new InvalidAppointmentIdException(
	                "Appointment not found with ID: " + appointmentId));
	        List<Consultation> consultations = consultationRepository.findByAppointmentAppointmentId(appointmentId);
	        List<ConsultationResponseDTO> consultationDTOs = new ArrayList<>();
	        for (Consultation consultation : consultations) {
	            consultationDTOs.add(mapToDTO(consultation));
	        }
	 
	        return consultationDTOs;
	 }
}