package com.smtpmail.entity;

import lombok.Data;

@Data
public class UserRegister extends User {

	private String authCode;
}
