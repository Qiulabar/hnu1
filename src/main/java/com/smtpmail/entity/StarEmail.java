package com.smtpmail.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StarEmail {
	private String id;

	/**
	 * 星标邮件所属用户名
	 */
	private String belongUserName;
}
