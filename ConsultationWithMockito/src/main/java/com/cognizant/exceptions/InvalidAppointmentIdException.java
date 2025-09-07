package com.cognizant.exceptions;

public class InvalidAppointmentIdException extends RuntimeException {
	private String msg;

	public InvalidAppointmentIdException(String AppointmentId) {
		super();
		this.msg = AppointmentId;

	}

	public String getMsg() {
		return msg;
	}

}