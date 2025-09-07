package com.cognizant.service;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.cognizant.dto.ConsultationResponseDTO;
import com.cognizant.exceptions.InvalidAppointmentIdException;

public interface IConsultationService {

	/**
	 * Creates a new consultation record.
	 *
	 * @param consultationDTO DTO containing consultation details.
	 * @return The created ConsultationResponseDTO.
	 * @throws InvalidAppointmentIdException If the associated appointment is not
	 *                                       found.
	 */
	ConsultationResponseDTO createConsultation(ConsultationResponseDTO consultationDTO);

	/**
	 * Retrieves a consultation by its ID. This method is named
	 * `getConsultationDTOById` as requested for the controller.
	 *
	 * @param consultationId  The ID of the consultation.
	 * @param consultationDTO
	 * @return An Optional containing the ConsultationResponseDTO if found.
	 */
	ConsultationResponseDTO getConsultationDTOById(Long consultationId);

	/**
	 * Updates an existing consultation record.
	 *
	 * @param consultationId  The ID of the consultation to update.
	 * @param consultationDTO DTO containing updated consultation details.
	 * @return The updated ConsultationResponseDTO.
	 * @throws ConsultationNotFoundException If the consultation to update is not
	 *                                       found.
	 */
	ConsultationResponseDTO updateConsultation(Long consultationId, ConsultationResponseDTO consultationDTO);

	List<ConsultationResponseDTO> getConsultationsByAppointmentId(Long appointmentId);

}