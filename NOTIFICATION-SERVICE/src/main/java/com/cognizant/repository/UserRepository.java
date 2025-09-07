package com.cognizant.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cognizant.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

}
