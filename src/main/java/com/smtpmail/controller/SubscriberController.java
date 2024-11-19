package com.smtpmail.controller;

import com.smtpmail.common.Result;
import com.smtpmail.entity.Publisher;
import com.smtpmail.entity.Subscriber;
import com.smtpmail.mapper.PublisherMapper;
import com.smtpmail.mapper.SubscriberMapper;
import com.smtpmail.util.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/smtpmail/subscriber")
public class SubscriberController {
	@Autowired
	SubscriberMapper subscriberMapper;
	@Autowired
	PublisherMapper publisherMapper;
	@Autowired
	JWTUtil jwtUtil;

	/**
	 * 用户订阅publisher
	 */
	@GetMapping
	public Result subscribe(@RequestParam String publisher) {
		Publisher p = publisherMapper.select(publisher);
		if (p == null) {
			return Result.error().message("不存在的publisher");
		}
		String username = jwtUtil.getUserToken().getUsername();
		Subscriber subscriber = new Subscriber(publisher, username);
		Subscriber s = subscriberMapper.select(subscriber);
		if (s != null) {
			return Result.error().message("已经订阅，无法再次订阅");
		}
		subscriberMapper.save(subscriber);
		return Result.ok().message("订阅成功");
	}

	/**
	 * 用户取消订阅publisher
	 */
	@DeleteMapping
	public Result cancelSubscribe(@RequestParam String publisher) {
		String username = jwtUtil.getUserToken().getUsername();
		Subscriber subscriber = new Subscriber(publisher, username);
		subscriberMapper.delete(subscriber);
		return Result.ok();
	}
}
