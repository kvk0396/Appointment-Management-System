package com.cognizant.exceptions;

public class TimeSlotUnavailableException extends RuntimeException {
	private String msg;

	public TimeSlotUnavailableException(String msg) {
		super();
		this.msg = msg;
	}

	public String getMsg() {
		return msg;
	}

}
