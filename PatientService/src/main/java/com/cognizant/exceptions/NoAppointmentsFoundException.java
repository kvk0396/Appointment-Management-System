package com.cognizant.exceptions;

public class NoAppointmentsFoundException extends RuntimeException {
	String message;

	
	public NoAppointmentsFoundException(String message) {
		super(message);
	}

	public NoAppointmentsFoundException() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getMsg() {
		return message;
	}
	
}
