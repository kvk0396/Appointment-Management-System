package com.cognizant.exceptions;

public class InvalidAppointmentException extends RuntimeException {
	private String msg;

	public InvalidAppointmentException(String msg) {
		super();
		this.msg = msg;
	}

	public String getMsg() {
		return msg;
	}

}
