package com.cognizant.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;

import com.cognizant.dto.AppointmentDTO;
import com.cognizant.dto.UserDTO;
import com.cognizant.entity.Appointment;
import com.cognizant.entity.User;

public class AppointmentMapper {

	public static final ModelMapper modelMapper = new ModelMapper();
	
	public static AppointmentDTO convertToDto(Appointment appointment) {
		return modelMapper.map(appointment, AppointmentDTO.class);	
	}
	
	public static Appointment convertToEntity(AppointmentDTO appointment) {
		return modelMapper.map(appointment, Appointment.class);
	}
	
	public static List<AppointmentDTO> convertToDtoList(List<Appointment> appointments){
		
		return appointments.stream()
				.map(AppointmentMapper::convertToDto)
				.collect(Collectors.toList());
		
	}
	
	public static List<Appointment> convertToEntityList(List<AppointmentDTO> appointments){
			
			return appointments.stream()
					.map(AppointmentMapper::convertToEntity)
					.collect(Collectors.toList());
			
	}
		
}
