package com.cognizant.service;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.cognizant.dto.UserDTO;
import com.cognizant.entity.User;
import com.cognizant.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public UserService(UserRepository userRepository, ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    public UserDTO createUser(UserDTO userDto) {
        User user = modelMapper.map(userDto, User.class);
        User saved = userRepository.save(user);
        return modelMapper.map(saved, UserDTO.class);
    }
}
