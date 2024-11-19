package com.smtpmail.util;

import com.smtpmail.entity.Email;
import com.smtpmail.smtp.server.SmtpSession;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class EmailAnalyzer {

	/**
	 * 查找第一次出现\n\n的子字符串的初始位置
	 * <p>如果没有找到，则返回-1
	 */
	private int findHeadIndex(String data) {
		if (data != null) {
			return data.indexOf("\n\n");
		}
		return -1;
	}

	/**
	 * 将邮件头解析成为一个个key-value对
	 */
	private Map<String, String> analyseHead(String dataHead) {
		Map<String, String> map = new HashMap<>();
		StringTokenizer tokenizer = new StringTokenizer(dataHead, "\n", false);
		while (tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken();
			int pointIndex = token.indexOf(':');
			if (pointIndex != -1) {
				map.put(token.substring(0, pointIndex).toUpperCase(),
						token.substring(pointIndex+1).trim());
			}
		}
		return map;
	}

	/**
	 * 解析邮件对象：主要是解析邮件中的data，将data中的数据转化为from、to、subject
	 */
	public void analyseEmail(Email email, SmtpSession smtpSession) {
		String data = email.getData();
		int headIndex = findHeadIndex(data);
		if (headIndex == -1) {
			email.setFrom(null);
			email.setTo(null);
			email.setSubject(null);
		} else {
			Map<String, String> headMap = analyseHead(data.substring(0, headIndex));
			email.setFrom(headMap.get("FROM"));
			email.setTo(headMap.get("TO"));
			email.setSubject(headMap.get("SUBJECT"));
			email.setData(data.substring(headIndex + 2));
		}
		// 设置日期
		email.setDate(new Date());
		// 设置datagram
		String ip = ChannelUtil.getRemoteIpAsString(smtpSession.channel);
		String datagramBuilder =
				"Received: from " + email.getHelo() + " ([" + ip + "]) \r\n"
				+ "by smtp.lunangangster.store with SMTP;" + email.getDate().toString() + "\r\n"
				+ "Message-ID: " + email.getId() + "\r\n"
				+ "Sender: <" + email.getMailFrom() + ">\r\n"
				+ email.getData();
		email.setDatagram(datagramBuilder);
		email.setIp(ip);
		email.setLength(email.getDatagram().getBytes(StandardCharsets.UTF_8).length);
	}
}
