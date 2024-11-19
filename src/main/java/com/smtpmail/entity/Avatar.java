package com.smtpmail.entity;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Avatar {
	private String username;

	private String avatar;

	private Date createTime;
}
