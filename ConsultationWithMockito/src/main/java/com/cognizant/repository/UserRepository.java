package com.cognizant.repository;

import com.cognizant.entity.User;
import com.cognizant.enums.Role;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	@Query("SELECT u FROM User u WHERE u.userId = :id AND u.role = :role")
    Optional<User> findByIdAndRole(@Param("id") Long id, @Param("role") Role role);
}