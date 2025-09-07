package com.cognizant.exceptions;

import com.fasterxml.jackson.annotation.JsonFormat; // Import for date formatting
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Data
public class ErrorInfo {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS") // Corrected pattern for timezone
    private LocalDateTime timestamp;
    private Integer status;
    private String error; // Renamed from httpStatus to align with desired output
    private String message; // Renamed from errorMessage
    private String path; // New field for the request path

    public ErrorInfo() {
        this.timestamp = LocalDateTime.now();
    }

    // You can add a constructor for convenience if needed, but setters are fine.
    public ErrorInfo(HttpStatus status, String message, String path) {
        this(); // Calls the default constructor to set timestamp
        this.status = status.value();
        this.error = status.getReasonPhrase(); // Get reason phrase for "error" field
        this.message = message;
        this.path = path;
    }
}