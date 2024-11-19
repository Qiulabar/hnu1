package com.smtpmail.controller;

import com.smtpmail.common.LogType;
import com.smtpmail.common.Result;
import com.smtpmail.util.FileUtil;
import com.smtpmail.util.JWTUtil;
import com.smtpmail.util.LogFileUtil;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/smtpmail/log")
@Slf4j
public class LogController {
	@Autowired
	LogFileUtil logFileUtil;
	@Autowired
	JWTUtil jwtUtil;
	@Autowired
	HttpServletResponse response;

	/**
	 * 管理员接口：获取日志文件名列表
	 */
	@GetMapping("/nameList")
	public Result getFileNameList() {
		// if (!jwtUtil.isManager()) {
		// 	log.error("【{}】用户尝试获取所有管理员，失败原因：无权限", jwtUtil.getUserToken().getUsername());
		// 	return Result.error().message("无权限");
		// }
		Map<String, Object> resultMap = new HashMap<>();
		List<String> infoLogs = logFileUtil.getFilesNameFromDirectory(
				logFileUtil.infoFilePath);
		List<String> warnLogs = logFileUtil.getFilesNameFromDirectory(
				logFileUtil.warnFilePath);
		List<String> errorLogs = logFileUtil.getFilesNameFromDirectory(
				logFileUtil.errorFilePath);
		List<String> debugLogs = logFileUtil.getFilesNameFromDirectory(
				logFileUtil.debugFilePath);
		resultMap.put("info", infoLogs);
		resultMap.put("warn", warnLogs);
		resultMap.put("error", errorLogs);
		resultMap.put("debug", debugLogs);
		return Result.ok().data(resultMap);
	}

	/**
	 * 管理员接口：根据文件类型和文件名获取文件流
	 */
	@GetMapping("/{logType}/{fileName}")
	public Result getFileByName(@PathVariable String logType, @PathVariable String fileName) {
		// if (!jwtUtil.isManager()) {
		// 	log.error("【{}】用户尝试获取所有管理员，失败原因：无权限", jwtUtil.getUserToken().getUsername());
		// 	return Result.error().message("无权限");
		// }
		LogType type;
		try {
			type = LogType.valueOf(logType.toUpperCase(Locale.ROOT));
		} catch (IllegalArgumentException e) {
			return Result.error().message("不存在的日志文件类型：" + logType);
		}
		if (!logFileUtil.isFileExist(type, fileName)) {
			return Result.error().message("不存在的日志文件");
		}
		File file = logFileUtil.getFileFromName(type, fileName);
		FileUtil.copyStream(file, response);
		return null;
	}

	/**
	 * 管理员接口：根据文件类型和文件名获取文件流
	 */
	@GetMapping("/nohup.out")
	public Result getNohup() {
		// if (!jwtUtil.isManager()) {
		// 	log.error("【{}】用户尝试获取所有管理员，失败原因：无权限", jwtUtil.getUserToken().getUsername());
		// 	return Result.error().message("无权限");
		// }
		File file = new File("/root/jar/nohup.out");
		FileUtil.copyStream(file, response);
		return null;
	}
}
