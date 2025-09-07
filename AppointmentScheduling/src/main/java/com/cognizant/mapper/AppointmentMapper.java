package com.cognizant.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;

import com.cognizant.dto.AppointmentResponseDTO;
import com.cognizant.entity.Appointment;

public class AppointmentMapper {
	 
	public static final ModelMapper modelMapper = new ModelMapper();
	
	public static AppointmentResponseDTO convertToDto(Appointment appointment) {
		AppointmentResponseDTO appointmentResponseDTO = modelMapper.map(appointment, AppointmentResponseDTO.class);
		appointmentResponseDTO.setDoctor(UserMapper.convertToDto(appointment.getDoctor()));
		appointmentResponseDTO.setPatient(UserMapper.convertToDto(appointment.getPatient()));
		return appointmentResponseDTO;
	}
	
	public static Appointment convertToEntity(AppointmentResponseDTO appointment) {
		return modelMapper.map(appointment, Appointment.class);
	}
	
	public static List<AppointmentResponseDTO> convertToDtoList(List<Appointment> appointments){
		
		return appointments.stream()
				.map(AppointmentMapper::convertToDto)
				.collect(Collectors.toList());
		
	}
	
	public static List<Appointment> convertToEntityList(List<AppointmentResponseDTO> appointments){
			
			return appointments.stream()
					.map(AppointmentMapper::convertToEntity)
					.collect(Collectors.toList());
			
	}
		
}