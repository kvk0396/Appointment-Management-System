package com.cognizant.dto;
import com.cognizant.enums.Status;
import lombok.Data;

@Data
public class AppointmentResponseDTO {
    private Long appointmentId;
    private Long patientId;
    private String patientName;
    private Long doctorId;
    private String doctorName;
    private Status status;
}
         
         