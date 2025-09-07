package com.cognizant.exceptions;

public class InvalidConsultationIdException extends RuntimeException {
    private final String msg;

    public InvalidConsultationIdException(String msg) {
        super(msg);
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }
}