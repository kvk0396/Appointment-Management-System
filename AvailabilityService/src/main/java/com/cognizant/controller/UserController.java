package com.cognizant.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.cognizant.dto.UserDTO;
import com.cognizant.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Create User (Doctor or Patient)
    @PostMapping
    public ResponseEntity<UserDTO> createUser(@RequestBody UserDTO userDto) {
        UserDTO createdUser = userService.createUser(userDto);
        return ResponseEntity.ok(createdUser);
    }
}