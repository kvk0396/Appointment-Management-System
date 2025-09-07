package com.cognizant.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Entity

public class EmailEntity {

	@Id
	@Email
	private String toEmail;

	
	private String subject;
	
	private String body;
}
