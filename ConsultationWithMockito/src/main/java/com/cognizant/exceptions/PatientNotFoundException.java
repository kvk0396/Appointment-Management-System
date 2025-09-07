package com.cognizant.exceptions;

public class PatientNotFoundException extends RuntimeException {
	private String msg;

	public PatientNotFoundException(String msg) {
		super();
		this.msg = msg;

	}

	public String getMsg() {
		return msg;
	}

}