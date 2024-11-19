package com.smtpmail.exception;

/**
 * 不合法的端口号
 */
public class IllegalPortException extends RuntimeException{
	public IllegalPortException() {
		super("未授权的端口号");
	}

	public IllegalPortException(String message) {
		super(message);
	}
}
