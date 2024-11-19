package com.smtpmail.controller;

import com.smtpmail.common.Result;
import com.smtpmail.entity.Advice;
import com.smtpmail.service.AdviceService;
import com.smtpmail.util.JWTUtil;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/smtpmail/advice")
public class AdviceController {
	@Autowired
	AdviceService adviceService;
	@Autowired
	ExecutorService threadPool;
	@Autowired
	JWTUtil jwtUtil;

	/**
	 * 给官方的建议
	 * advice: advice
	 */
	@PostMapping
	public Result giveAdvice(@RequestBody Advice advice) {
		String username = jwtUtil.getUserToken().getUsername();
		threadPool.execute(() -> {
			advice.setUsername(username);
			adviceService.save(advice);
		});
		return Result.ok();
	}
}
