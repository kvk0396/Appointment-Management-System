package com.cognizant.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class TimeSlotNotFoundException extends RuntimeException {
	
	String message;
    public TimeSlotNotFoundException() {
        super();
    }
    
    public TimeSlotNotFoundException(String message) {
    	super(message);
    }

    public TimeSlotNotFoundException(Long timeSlotId) {
        super("Time slot not found with ID: " + timeSlotId);
    }
}