package com.smtpmail.exception;

public class UnLoginException extends RuntimeException {

	public UnLoginException() {
		super("请先登录");
	}

	public UnLoginException(String message) {
		super(message);
	}
}
