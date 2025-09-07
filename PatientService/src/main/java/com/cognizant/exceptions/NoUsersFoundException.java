package com.cognizant.exceptions;

public class NoUsersFoundException extends RuntimeException{

	String message;

	
	
	public NoUsersFoundException() {
		super();
		// TODO Auto-generated constructor stub
	}

	public NoUsersFoundException(String message) {
		super(message);
	}

	public String getMsg() {
		return message;
	}
	
	

	
}
