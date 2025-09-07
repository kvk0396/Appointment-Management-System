package com.cognizant.exceptions;

public class DoctorNotFoundException extends RuntimeException {
	private String msg;

	public DoctorNotFoundException(String msg) {
		super();
		this.msg = msg;

	}

	public String getMsg() {
		return msg;
	}

}