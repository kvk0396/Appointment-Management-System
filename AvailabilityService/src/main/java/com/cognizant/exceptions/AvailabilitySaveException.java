package com.cognizant.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class AvailabilitySaveException extends AvailabilityException {
    public AvailabilitySaveException(String message, Throwable cause) {
        super("Error saving availability: " + message, cause);
    }

	public AvailabilitySaveException() {
		super();
		// TODO Auto-generated constructor stub
	}
}