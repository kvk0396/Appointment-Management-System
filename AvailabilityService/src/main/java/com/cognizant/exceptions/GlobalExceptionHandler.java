package com.cognizant.exceptions;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

//@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(DoctorNotFoundException.class)
    public ResponseEntity<ErrorInfo> handleDoctorNotFoundException(DoctorNotFoundException ex, WebRequest request) {
        ErrorInfo errorInfo = new ErrorInfo();
        errorInfo.setErrorMessage(ex.getMessage());
        errorInfo.setHttpStatus(HttpStatus.NOT_FOUND);
        errorInfo.setLocalDateTime(LocalDateTime.now());
        return new ResponseEntity<>(errorInfo, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AvailabilityNotFoundException.class)
    public ResponseEntity<ErrorInfo> handleAvailabilityNotFoundException(AvailabilityNotFoundException ex, WebRequest request) {
        ErrorInfo errorInfo = new ErrorInfo();
        errorInfo.setErrorMessage(ex.getMessage());
        errorInfo.setHttpStatus(HttpStatus.NOT_FOUND);
        errorInfo.setLocalDateTime(LocalDateTime.now());
        return new ResponseEntity<>(errorInfo, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(TimeSlotNotFoundException.class)
    public ResponseEntity<ErrorInfo> handleTimeSlotNotFoundException(TimeSlotNotFoundException ex, WebRequest request) {
        ErrorInfo errorInfo = new ErrorInfo();
        errorInfo.setErrorMessage(ex.getMessage());
        errorInfo.setHttpStatus(HttpStatus.NOT_FOUND);
        errorInfo.setLocalDateTime(LocalDateTime.now());
        return new ResponseEntity<>(errorInfo, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidTimeRangeException.class)
    public ResponseEntity<ErrorInfo> handleInvalidTimeRangeException(InvalidTimeRangeException ex, WebRequest request) {
        ErrorInfo errorInfo = new ErrorInfo();
        errorInfo.setErrorMessage(ex.getMessage());
        errorInfo.setHttpStatus(HttpStatus.BAD_REQUEST);
        errorInfo.setLocalDateTime(LocalDateTime.now());
        return new ResponseEntity<>(errorInfo, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidTimeSlotException.class)
    public ResponseEntity<ErrorInfo> handleInvalidTimeSlotException(InvalidTimeSlotException ex, WebRequest request) {
        ErrorInfo errorInfo = new ErrorInfo();
        errorInfo.setErrorMessage(ex.getMessage());
        errorInfo.setHttpStatus(HttpStatus.BAD_REQUEST);
        errorInfo.setLocalDateTime(LocalDateTime.now());
        return new ResponseEntity<>(errorInfo, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AvailabilitySaveException.class)
    public ResponseEntity<ErrorInfo> handleAvailabilitySaveException(AvailabilitySaveException ex, WebRequest request) {
        ErrorInfo errorInfo = new ErrorInfo();
        errorInfo.setErrorMessage(ex.getMessage());
        errorInfo.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        errorInfo.setLocalDateTime(LocalDateTime.now());
        // Consider logging the exception details
        return new ResponseEntity<>(errorInfo, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(AvailabilityUpdateException.class)
    public ResponseEntity<ErrorInfo> handleAvailabilityUpdateException(AvailabilityUpdateException ex, WebRequest request) {
        ErrorInfo errorInfo = new ErrorInfo();
        errorInfo.setErrorMessage(ex.getMessage());
        errorInfo.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        errorInfo.setLocalDateTime(LocalDateTime.now());
        // Consider logging the exception details
        return new ResponseEntity<>(errorInfo, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // Fallback for any other unhandled RuntimeExceptions
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorInfo> handleGenericException(RuntimeException ex, WebRequest request) {
        ErrorInfo errorInfo = new ErrorInfo();
        errorInfo.setErrorMessage("An unexpected error occurred.");
        errorInfo.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        errorInfo.setLocalDateTime(LocalDateTime.now());
        // Log the exception details for debugging
        return new ResponseEntity<>(errorInfo, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}