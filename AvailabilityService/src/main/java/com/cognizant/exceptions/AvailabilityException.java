package com.cognizant.exceptions;

// Base exception for the availability module
public class AvailabilityException extends RuntimeException {
    public AvailabilityException() {
        super();
       
    }

    public AvailabilityException(String message) {
        super(message);
    }

    public AvailabilityException(String message, Throwable cause) {
        super(message, cause);
    }
}
