package com.smtpmail.entity;

import java.util.Date;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 发布者
 */
@Data
@NoArgsConstructor
public class Publisher {
	private String username;

	/**
	 * 状态
	 * -1: 拒绝
	 * 0：审核中
	 * 1：已通过
	 */
	private Integer state;

	private String avatar;

	private Date ackTime;

	private Date auditTime;

	public Publisher(String username, Integer state) {
		this.username = username;
		this.state = state;
	}
}
