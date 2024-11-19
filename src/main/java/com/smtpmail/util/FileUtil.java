package com.smtpmail.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import javax.servlet.http.HttpServletResponse;
import org.springframework.util.StringUtils;

public class FileUtil {
	public static String getFileSuffix(String fileName) {
		if (!StringUtils.hasText(fileName)) {
			return "";
		}
		return fileName.substring(fileName.lastIndexOf('.'));
	}

	public static void copyStream(File file, HttpServletResponse response) {
		FileInputStream fis = null;
		OutputStream os = null;
		BufferedReader reader = null;
		BufferedWriter writer = null;
		try {
			fis = new FileInputStream(file);
			os = response.getOutputStream();
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/-download");
			response.addHeader("Content-Disposition", "attachment;filename=" + file.getName());
			reader = new BufferedReader(new InputStreamReader(fis));
			writer = new BufferedWriter(new OutputStreamWriter(os));
			char[] chars = new char[1024];
			int len = -1;
			while ((len = reader.read(chars)) != -1) {
				writer.write(chars, 0, len);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				writer.flush();
				reader.close();
				fis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
