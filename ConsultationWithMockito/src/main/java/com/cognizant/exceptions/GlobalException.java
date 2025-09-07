package com.cognizant.exceptions;

import jakarta.servlet.http.HttpServletRequest; // Import HttpServletRequest
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalException {

    @ExceptionHandler(AppointmentNotFoundException.class)
    public ResponseEntity<ErrorInfo> handleAppointmentNotFoundException(AppointmentNotFoundException e, HttpServletRequest request) {
        return buildErrorResponse(e.getMsg(), HttpStatus.NOT_FOUND, request.getRequestURI()); // Changed to NOT_FOUND for consistency
    }

    @ExceptionHandler(DoctorNotFoundException.class)
    public ResponseEntity<ErrorInfo> handleDoctorNotFoundException(DoctorNotFoundException e, HttpServletRequest request) {
        return buildErrorResponse(e.getMsg(), HttpStatus.NOT_FOUND, request.getRequestURI()); // Changed to NOT_FOUND
    }

    @ExceptionHandler(PatientNotFoundException.class)
    public ResponseEntity<ErrorInfo> handlePatientNotFoundException(PatientNotFoundException e, HttpServletRequest request) {
        return buildErrorResponse(e.getMsg(), HttpStatus.NOT_FOUND, request.getRequestURI()); // Changed to NOT_FOUND
    }

    @ExceptionHandler(InvalidAppointmentIdException.class)
    public ResponseEntity<ErrorInfo> handleInvalidAppointmentIdException(InvalidAppointmentIdException e, HttpServletRequest request) {
        return buildErrorResponse(e.getMsg(), HttpStatus.BAD_REQUEST, request.getRequestURI());
    }

    @ExceptionHandler(InvalidConsultationIdException.class)
    public ResponseEntity<ErrorInfo> handleConsultationNotFoundException(InvalidConsultationIdException e, HttpServletRequest request) {
        return buildErrorResponse(e.getMsg(), HttpStatus.NOT_FOUND, request.getRequestURI());
    }

    // Add a general exception handler for anything else
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorInfo> handleGenericException(Exception e, HttpServletRequest request) {
        // Log the exception for debugging purposes
        e.printStackTrace();
        return buildErrorResponse("An unexpected error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, request.getRequestURI());
    }


    private ResponseEntity<ErrorInfo> buildErrorResponse(String message, HttpStatus status, String path) {
        ErrorInfo errorInfo = new ErrorInfo(status, message, path);
        return new ResponseEntity<>(errorInfo, status);
    }
}