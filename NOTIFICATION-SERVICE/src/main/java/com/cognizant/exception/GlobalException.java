package com.cognizant.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalException {

	  @ExceptionHandler(value = UserNotFoundException.class)
		public ResponseEntity<ErrorInfo> handleException(UserNotFoundException userNotFoundException){
		  ErrorInfo errorObject= new ErrorInfo();
		  errorObject.setErrorMessage(userNotFoundException.getMsg());
		  errorObject.setLocalDateTime(LocalDateTime.now());
		  errorObject.setHttpStatus(HttpStatus.BAD_REQUEST);
			return new ResponseEntity<ErrorInfo>(errorObject, HttpStatus.BAD_REQUEST);
		}
}
