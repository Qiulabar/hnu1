package com.smtpmail.util;

import com.smtpmail.common.LogType;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class LogFileUtil {
	@Value("${logging.file.path}")
	private String url;

	@Value("${logging.file.info}")
	public String infoFilePath;

	@Value("${logging.file.warn}")
	public String warnFilePath;

	@Value("${logging.file.error}")
	public String errorFilePath;

	@Value("${logging.file.debug}")
	public String debugFilePath;

	private File directory;

	/**
	 * 获取指定类型的日志文件的所有文件名
	 */
	public List<String> getFilesNameFromDirectory(String path) {
		directory = new File(path);
		if (!directory.isDirectory() || directory.listFiles() == null) {
			throw new RuntimeException("服务器内部错误：日志系统错误");
		}
		File[] files = directory.listFiles();
		assert files != null;
		List<String> logFileNames = new ArrayList<>(files.length);
		for (File file : files) {
			logFileNames.add(file.getName());
		}
		return logFileNames;
	}

	/**
	 * 根据文件目录获取其所有子文件
	 */
	public File[] getFilesFromDirectory(String path) {
		directory = new File(path);
		if (!directory.isDirectory()) {
			throw new RuntimeException("服务器内部错误：日志系统错误");
		}
		return directory.listFiles();
	}

	/**
	 * 根据文件名获取对应文件
	 */
	public File getFileFromName(LogType logType, String fileName) {
		String preFileName = "";
		switch (logType) {
			case INFO: preFileName = infoFilePath;break;
			case WARN: preFileName = warnFilePath;break;
			case ERROR: preFileName = errorFilePath;break;
			case DEBUG: preFileName = debugFilePath;break;
		}
		File file = new File(preFileName + "/" + fileName);
		if (!file.exists() || !file.isFile()) {
			return null;
		}
		return file;
	}

	public boolean isFileExist(LogType logType, String fileName) {
		String directoryPath = this.getPathFromLogType(logType);
		String fullPath = directoryPath + "/" + fileName;
		File file = new File(fullPath);
		if (!file.exists() || !file.isFile()) {
			return false;
		}
		return true;
	}

	public String getPathFromLogType(LogType logType) {
		if (logType != null) {
			switch (logType) {
				case INFO: return infoFilePath;
				case WARN: return warnFilePath;
				case ERROR: return errorFilePath;
				case DEBUG: return debugFilePath;
			}
		}
		return null;
	}
}
