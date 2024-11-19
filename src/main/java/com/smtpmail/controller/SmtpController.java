package com.smtpmail.controller;

import com.smtpmail.common.Result;
import com.smtpmail.service.SmtpServer;
import com.smtpmail.util.JWTUtil;
import com.smtpmail.util.NetUtil;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/smtpmail/smtp")
@Slf4j
public class SmtpController {
	@Autowired
	JWTUtil jwtUtil;
	@Autowired
	NetUtil netUtil;

	/**
	 * 管理员接口：开启smtp服务
	 */
	@GetMapping("/start")
	public Result start() {
		String managerName = jwtUtil.getUserToken().getUsername();
		if (!jwtUtil.isManager()) {
			log.error("【{}】用户尝试开启smtp服务，失败原因：无权限", managerName);
			return Result.error().message("无权限");
		}
		if (SmtpServer.isRunning()) {
			log.error("【{}】用户尝试开启smtp服务，失败原因：SmtpServer Start Error: Already running on Port {}", managerName, SmtpServer.getRunningPort());
			return Result.error().message("SmtpServer Start Error: Already running on Port 25");
		}
		SmtpServer.start(25);
		log.info("【{}】用户开启smtp服务成功", managerName);
		return Result.ok().message("Started SmtpServer on Port 25 Successfully");
	}

	/**
	 * 管理员接口：停止smtp服务
	 */
	@GetMapping("/stop")
	public Result stop() {
		String managerName = jwtUtil.getUserToken().getUsername();
		if (!jwtUtil.isManager()) {
			log.error("【{}】用户尝试停止smtp服务，失败原因：无权限", managerName);
			return Result.error().message("无权限");
		}
		if (!SmtpServer.isRunning()) {
			log.error("【{}】用户尝试停止smtp服务，失败原因：SmtpServer Stop Error: SmtpServer is Not Running", managerName);
			return Result.error().message("SmtpServer Stop Error: SmtpServer is Not Running");
		}
		SmtpServer.stop();
		log.info("【{}】用户开启smtp服务成功", managerName);
		return Result.ok().message("SmtpServer is Stopped Successfully");
	}

	/**
	 * 管理员接口：查看Smtp服务状态
	 */
	@GetMapping("/status")
	public Result getServerStatus() {
		if (!jwtUtil.isManager()) {
			return Result.error().message("无权限");
		}
		Map<String, Object> resultMap = new HashMap<>();
		resultMap.put("status", SmtpServer.isRunning()? 1 : 0);
		resultMap.put("runningPort", SmtpServer.getRunningPort());
		return Result.ok().data(resultMap);
	}

	/**
	 * 管理员接口：设置Smtp服务的端口号
	 */
	@GetMapping("/port")
	public Result setServerPort(int port) {
		String managerName = jwtUtil.getUserToken().getUsername();
		if (!jwtUtil.isManager()) {
			log.error("【{}】用户尝试设置smtp服务端口号，失败原因：无权限", managerName);
			return Result.error().message("无权限");
		}
		if (port != 25 && (port < 30 || port > 109)) {
			log.error("【{}】用户尝试设置smtp服务端口号为{}, 失败原因：端口号{}未授权运行smtp服务", managerName, port, port);
			return Result.error().message("当前端口号未授权运行Smtp服务，Smtp服务端口授权区间：25 || [30, 109]");
		}
		if (netUtil.isPortUsing(port)) {
			log.error("【{}】用户尝试设置smtp服务端口号为{}，失败原因：端口号{}已经被占用", managerName, port, port);
			return Result.error().message("当前端口号已经被占用");
		}
		SmtpServer.stop();
		while (SmtpServer.isRunning()) {
		}
		SmtpServer.start(port);
		log.info("【{}】用户成功设置smtp服务端口号：{}", managerName, port);
		return Result.ok().message("Started SmtpServer on Port " + port);
	}
}
