package com.cognizant.exceptions;

public class RoleMismatchException extends RuntimeException{

	String message;

	
	
	
	public RoleMismatchException() {
		super();
		// TODO Auto-generated constructor stub
	}

	public RoleMismatchException(String message) {
		super(message);
	}

	public String getMsg() {
		return message;
	}
	
}
