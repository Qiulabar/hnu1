package com.smtpmail.entity;

import java.util.Date;
import lombok.Data;

@Data
public class PublisherShow {
	private String username;

	/**
	 * 状态
	 * -1: 拒绝
	 * 0：审核中
	 * 1：已通过
	 */
	private Integer state;

	private Date auditTime;
}
