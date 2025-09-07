package com.cognizant.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class AvailabilityUpdateException extends AvailabilityException {
    public AvailabilityUpdateException(String message, Throwable cause) {
        super("Error updating availability: " + message, cause);
    }

	public AvailabilityUpdateException() {
		super();
		// TODO Auto-generated constructor stub
	}
    
}