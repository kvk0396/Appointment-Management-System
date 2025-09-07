package com.cognizant.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class AvailabilityNotFoundException extends RuntimeException {
    public AvailabilityNotFoundException() {
        super();
    }

    public AvailabilityNotFoundException(Long doctorId, java.time.LocalDate date) {
        super("Availability not found for Doctor ID: " + doctorId + " on Date: " + date);
    }
}