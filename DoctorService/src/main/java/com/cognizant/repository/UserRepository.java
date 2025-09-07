package com.cognizant.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cognizant.dto.UserDTO;
import com.cognizant.entity.User;
import com.cognizant.enums.Role;

public interface UserRepository extends JpaRepository<User, Long> {

	Optional<List<User>> findByRole(Role doctor);

	Optional<User> findByEmail(String email);

}
