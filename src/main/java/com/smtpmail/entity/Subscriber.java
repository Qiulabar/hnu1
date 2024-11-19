package com.smtpmail.entity;

import java.util.Date;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 订阅者
 */
@Data
@NoArgsConstructor
public class Subscriber {
	private String publisher;

	private String subscriber;

	private Date createTime;

	public Subscriber(String publisher, String subscriber) {
		this.publisher = publisher;
		this.subscriber = subscriber;
		this.createTime = new Date();
	}
}
