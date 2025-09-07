package com.cognizant.exceptions;

public class AppointmentNotFoundException extends RuntimeException {
	private String msg;

	public AppointmentNotFoundException(String msg) {
		super();
		this.msg = msg;

	}

	public String getMsg() {
		return msg;
	}

}