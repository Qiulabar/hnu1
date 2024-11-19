package com.smtpmail.util;

import com.smtpmail.exception.IllegalPortException;
import java.io.IOException;
import java.net.ServerSocket;
import org.springframework.stereotype.Component;

/**
 * 用于测试本地端口是否被占用的工具类
 */
@Component
public class NetUtil {
	public boolean isPortUsing(int port) {
		if (port < 0) {
			throw new IllegalPortException();
		}
		boolean ans = false;
		ServerSocket ss = null;
		try {
			// 测试连接
			ss = new ServerSocket(port);
		} catch (IOException e) {
			ans = true;
		} finally {
			if (ss != null) {
				try {
					ss.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return ans;
	}
}
