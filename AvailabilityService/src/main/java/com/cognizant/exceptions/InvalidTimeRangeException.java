package com.cognizant.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidTimeRangeException extends RuntimeException {
    public InvalidTimeRangeException() {
        super();
    }

    public InvalidTimeRangeException(java.time.LocalTime startTime, java.time.LocalTime endTime) {
        super("Invalid time range: Start time (" + startTime + ") cannot be after or equal to end time (" + endTime + ").");
    }
}