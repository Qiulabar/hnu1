package com.smtpmail.controller;

import com.smtpmail.common.Result;
import com.smtpmail.service.Pop3Server;
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
@RequestMapping("/smtpmail/pop3")
@Slf4j
public class Pop3Controller {
	@Autowired
	JWTUtil jwtUtil;
	@Autowired
	NetUtil netUtil;

	/**
	 * 管理员接口：开启pop3服务
	 */
	@GetMapping("/start")
	public Result start() {
		String managerName = jwtUtil.getUserToken().getUsername();
		if (!jwtUtil.isManager()) {
			log.error("【{}】用户尝试开启pop3服务，失败原因：无权限", managerName);
			return Result.error().message("无权限");
		}
		if (Pop3Server.isRunning()) {
			log.error("【{}】用户尝试开启pop3服务，失败原因：Pop3Server Start Error: Already running on Port 110", managerName);
			return Result.error().message("Pop3Server Start Error: Already running on Port 110");
		}
		Pop3Server.start(110);
		log.info("【{}】用户开启pop3服务成功", managerName);
		return Result.ok().message("Started Pop3Server on Port 110 Successfully");
	}

	/**
	 * 管理员接口：停止pop3服务
	 */
	@GetMapping("/stop")
	public Result stop() {
		String managerName = jwtUtil.getUserToken().getUsername();
		if (!jwtUtil.isManager()) {
			log.error("【{}】用户尝试停止pop3服务，失败原因：无权限", managerName);
			return Result.error().message("无权限");
		}
		if (!Pop3Server.isRunning()) {
			log.error("【{}】用户尝试停止pop3服务，失败原因：Pop3Server Stop Error: Pop3Server is Not Running", managerName);
			return Result.error().message("Pop3Server Stop Error: Pop3Server is Not Running");
		}
		Pop3Server.stop();
		log.info("【{}】用户开启pop3服务成功", managerName);
		return Result.ok().message("Pop3Server is Stopped Successfully");
	}

	/**
	 * 管理员接口：查看Pop3服务状态
	 */
	@GetMapping("/status")
	public Result getServerStatus() {
		if (!jwtUtil.isManager()) {
			return Result.error().message("无权限");
		}
		Map<String, Object> resultMap = new HashMap<>();
		resultMap.put("status", Pop3Server.isRunning()? 1 : 0);
		resultMap.put("runningPort", Pop3Server.getRunningPort());
		return Result.ok().data(resultMap);
	}

	/**
	 * 管理员接口：设置Pop3服务的端口号
	 */
	@GetMapping("/port")
	public Result setServerPort(int port) {
		String managerName = jwtUtil.getUserToken().getUsername();
		if (!jwtUtil.isManager()) {
			log.error("【{}】用户尝试设置pop3服务端口号，失败原因：无权限", managerName);
			return Result.error().message("无权限");
		}
		if (port < 110 || port > 200) {
			log.error("【{}】用户尝试设置pop3服务端口号为{}, 失败原因：端口号{}未授权运行pop3服务", managerName, port, port);
			return Result.error().message("当前端口号未授权运行Pop3服务，Pop3服务端口授权区间：[110, 200]");
		}
		if (netUtil.isPortUsing(port)) {
			log.error("【{}】用户尝试设置pop3服务端口号为{}，失败原因：端口号{}已经被占用", managerName, port, port);
			return Result.error().message("当前端口号已经被占用");
		}
		Pop3Server.stop();
		while (Pop3Server.isRunning()) {
		}
		Pop3Server.start(port);
		log.info("【{}】用户成功设置pop3服务端口号：{}", managerName, port);
		return Result.ok().message("Started SmtpServer on Port " + port);
	}
}
