package com.cognizant.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;

import com.cognizant.dto.AppointmentDTO;
import com.cognizant.dto.UserDTO;
import com.cognizant.entity.Appointment;
import com.cognizant.entity.User;

public class UserMapper {

	public static final ModelMapper modelMapper = new ModelMapper();
	
	public static UserDTO convertToDto(User user) {
		return modelMapper.map(user, UserDTO.class);	
	}
	
	public static User convertToEntity(UserDTO user) {
		return modelMapper.map(user, User.class);
	}
	
	public static List<UserDTO> convertToDtoList(List<User> users){
		
		return users.stream()
				.map(UserMapper::convertToDto)
				.collect(Collectors.toList());
		
	}
	public static List<User> convertToEntityList(List<UserDTO> users){
		
		return users.stream()
				.map(UserMapper::convertToEntity)
				.collect(Collectors.toList());
		
	}
}
