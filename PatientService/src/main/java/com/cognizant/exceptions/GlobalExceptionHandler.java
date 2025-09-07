package com.cognizant.exceptions;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

	
	private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorInfo> handleUserNotFoundException(UserNotFoundException ex) {
        logger.error("UserNotFoundException caught: {}", ex.getMessage(), ex);
        ErrorInfo errorInfo = new ErrorInfo();
        errorInfo.setErrorMessage(ex.getMessage());
        errorInfo.setHttpStatus(HttpStatus.NOT_FOUND);
        errorInfo.setLocalDateTime(LocalDateTime.now());
        return new ResponseEntity<>(errorInfo, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(RoleMismatchException.class)
    public ResponseEntity<ErrorInfo> handleRoleMismatchException(RoleMismatchException ex) {
        logger.error("RoleMismatchException caught: {}", ex.getMessage(), ex);
        ErrorInfo errorInfo = new ErrorInfo();
        errorInfo.setErrorMessage(ex.getMessage());
        errorInfo.setHttpStatus(HttpStatus.FORBIDDEN); // Or HttpStatus.BAD_REQUEST depending on exact use case
        errorInfo.setLocalDateTime(LocalDateTime.now());
        return new ResponseEntity<>(errorInfo, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(NoUsersFoundException.class)
    public ResponseEntity<ErrorInfo> handleNoUsersFoundException(NoUsersFoundException ex) {
        logger.error("NoUsersFoundException caught: {}", ex.getMessage(), ex);
        ErrorInfo errorInfo = new ErrorInfo();
        errorInfo.setErrorMessage(ex.getMessage());
        errorInfo.setHttpStatus(HttpStatus.NO_CONTENT); // Or HttpStatus.NOT_FOUND if no content is considered a not found scenario
        errorInfo.setLocalDateTime(LocalDateTime.now());
        return new ResponseEntity<>(errorInfo, HttpStatus.NO_CONTENT);
    }

    @ExceptionHandler(NoAppointmentsFoundException.class)
    public ResponseEntity<ErrorInfo> handleNoAppointmentsFoundException(NoAppointmentsFoundException ex) {
        logger.error("NoAppointmentsFoundException caught: {}", ex.getMessage(), ex);
        ErrorInfo errorInfo = new ErrorInfo();
        errorInfo.setErrorMessage(ex.getMessage());
        errorInfo.setHttpStatus(HttpStatus.NO_CONTENT); // Or HttpStatus.NOT_FOUND
        errorInfo.setLocalDateTime(LocalDateTime.now());
        return new ResponseEntity<>(errorInfo, HttpStatus.NO_CONTENT);
    }

    // Generic exception handler for any other unhandled exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorInfo> handleGenericException(Exception ex) {
        logger.error("An unexpected error occurred: {}", ex.getMessage(), ex);
        ErrorInfo errorInfo = new ErrorInfo();
        errorInfo.setErrorMessage("An unexpected error occurred: " + ex.getMessage());
        errorInfo.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        errorInfo.setLocalDateTime(LocalDateTime.now());
        return new ResponseEntity<>(errorInfo, HttpStatus.INTERNAL_SERVER_ERROR);
    }
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationException(MethodArgumentNotValidException ex) { 
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });

        ErrorInfo errorInfo = new ErrorInfo();
        errorInfo.setErrorMessage("Validation Failed"); // A general message
        errorInfo.setHttpStatus(HttpStatus.BAD_REQUEST);
        errorInfo.setLocalDateTime(LocalDateTime.now());

        //  Include the detailed errors map in the response.  You might want to add this
        //  to the ErrorInfo class, or include it directly in the ResponseEntity.
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("errorInfo", errorInfo);
        responseBody.put("details", errors); // Include the field-specific errors

        return new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);
    }
	
}
