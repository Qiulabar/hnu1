package com.smtpmail.entity;

import lombok.Data;

@Data
public class UserUpdate extends UserRegister {
	private String newPassword;

	private String newPhone;
}
