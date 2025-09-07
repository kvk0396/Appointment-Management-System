package com.cognizant.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConsultationResponseDTO {
    private Long consultationId;
    private Long appointmentId;
    private String notes;
    private String prescription;
}