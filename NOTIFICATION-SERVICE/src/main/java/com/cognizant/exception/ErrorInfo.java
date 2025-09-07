package com.cognizant.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;

public class ErrorInfo {

	private String errorMessage;
	private HttpStatus httpStatus;
	private LocalDateTime localDateTime;
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	public HttpStatus getHttpStatus() {
		return httpStatus;
	}
	public void setHttpStatus(HttpStatus badRequest) {
		this.httpStatus = badRequest;
	}
	public LocalDateTime getLocalDateTime() {
		return localDateTime;
	}
	public void setLocalDateTime(LocalDateTime localDateTime) {
		this.localDateTime = localDateTime;
	}

}