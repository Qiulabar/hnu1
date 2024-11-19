package com.smtpmail.common;

public enum LogType {
	INFO("info"),
	WARN("warn"),
	ERROR("error"),
	DEBUG("debug");

	public String value;

	LogType(String value) {
		this.value = value;
	}
}
