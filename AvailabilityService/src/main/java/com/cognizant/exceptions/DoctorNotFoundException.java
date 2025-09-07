package com.cognizant.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class DoctorNotFoundException extends RuntimeException {
    public DoctorNotFoundException() {
        super();
    }

    public DoctorNotFoundException(Long doctorId) {
        super("Doctor not found with ID: " + doctorId);
    }
}