package com.cognizant.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Entity
@Data
@Table(name="emails")
public class Email {

	@Id
	private String toEmail;

	@NotBlank
	private String subject;
	
	private String body;
}
