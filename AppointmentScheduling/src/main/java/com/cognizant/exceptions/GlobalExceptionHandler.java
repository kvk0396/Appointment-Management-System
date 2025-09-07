package com.cognizant.exceptions;

import java.time.LocalDateTime;





import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;



@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorInfo> handleUserNotFound(UserNotFoundException ex) {
    	
    	ErrorInfo errorInfo = new ErrorInfo();
		errorInfo.setErrorMessage(ex.getMsg());
		errorInfo.setHttpStatus(HttpStatus.BAD_REQUEST);
		errorInfo.setLocalDateTime(LocalDateTime.now());
		
		return new ResponseEntity<ErrorInfo>(errorInfo, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(InvalidAppointmentException.class)
    public ResponseEntity<ErrorInfo> InvalidAppointment(InvalidAppointmentException ex) {
    	ErrorInfo errorInfo = new ErrorInfo();
		errorInfo.setErrorMessage(ex.getMsg());
		errorInfo.setHttpStatus(HttpStatus.BAD_REQUEST);
		errorInfo.setLocalDateTime(LocalDateTime.now());
		
    	
        return new ResponseEntity<ErrorInfo>(errorInfo,HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(TimeSlotUnavailableException.class)
    public ResponseEntity<ErrorInfo> handleTimeSlotUnavailable(TimeSlotUnavailableException ex) {
    	
    	ErrorInfo errorInfo = new ErrorInfo();
		errorInfo.setErrorMessage(ex.getMsg());
		errorInfo.setHttpStatus(HttpStatus.BAD_REQUEST);
		errorInfo.setLocalDateTime(LocalDateTime.now());
    	
        return new ResponseEntity<ErrorInfo>(errorInfo, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorInfo> handleGenericException(Exception ex) {
        ErrorInfo errorInfo = new ErrorInfo();
        errorInfo.setErrorMessage("An unexpected error occurred: " + ex.getMessage());
        errorInfo.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        errorInfo.setLocalDateTime(LocalDateTime.now());

        return new ResponseEntity<>(errorInfo, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    
    
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
			MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
    	ErrorInfo errorInfo = new ErrorInfo();
        errorInfo.setErrorMessage("An unexpected error occurred: " + ex.getMessage());
        errorInfo.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        errorInfo.setLocalDateTime(LocalDateTime.now());

		return new ResponseEntity<>(errorInfo,HttpStatus.BAD_REQUEST);
	}

}
