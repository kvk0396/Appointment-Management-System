package com.cognizant.exceptions;

public class UserNotFoundException extends RuntimeException {
	private String msg;

	public UserNotFoundException(String msg) {
		super();
		this.msg = msg;
	}

	public String getMsg() {
		return msg;
	}

}
