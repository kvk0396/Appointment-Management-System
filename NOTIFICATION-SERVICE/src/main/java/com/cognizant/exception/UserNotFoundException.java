package com.cognizant.exception;

public class UserNotFoundException extends Exception{
	String msg;

	public UserNotFoundException(String msg) {
		super();
		this.msg = msg;
	}

	public String getMsg() {
		return msg;
	}
	

}
