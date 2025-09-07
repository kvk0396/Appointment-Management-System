package com.cognizant.dto;



import lombok.Data;

@Data
public class ConsultationDTO {

	
    private Long consultationId;

    
    private Long appointmentId;

    private String notes;

    private String prescription;
	
}
