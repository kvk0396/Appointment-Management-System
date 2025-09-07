package com.cognizant.services;

import java.util.List;


import com.cognizant.dto.AppointmentRequestDto;
import com.cognizant.dto.AppointmentResponseDTO;
import com.cognizant.dto.AppointmentUpdateDTO;
import com.cognizant.entity.Appointment;


public interface IAppointmentService {
	//public Appointment delAppointment(Appointment appointment);
	public List<AppointmentResponseDTO> viewAll();
	public AppointmentResponseDTO viewByAppointmentId(Long appointmentId);
	public AppointmentResponseDTO addAppointment(AppointmentRequestDto appointmentDto);
	public AppointmentResponseDTO cancelAppointment(Long appointmentId);
	AppointmentResponseDTO updateAppointment(Long appointmentId, AppointmentUpdateDTO appointmentUpdateDTO);
	public AppointmentResponseDTO updateCompletion(Long appointmentId);
	Long getPatientIdForAppointment(Long appointmentId);
}
