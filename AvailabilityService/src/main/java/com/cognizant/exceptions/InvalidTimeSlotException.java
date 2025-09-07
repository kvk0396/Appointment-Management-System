package com.cognizant.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidTimeSlotException extends AvailabilityException {
    public InvalidTimeSlotException(String message) {
        super(message);
    }

	public InvalidTimeSlotException() {
		super();
		// TODO Auto-generated constructor stub
	}
}