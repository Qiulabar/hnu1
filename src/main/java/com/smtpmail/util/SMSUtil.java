package com.smtpmail.util;

import com.alibaba.fastjson.JSON;
import com.smtpmail.entity.SMSResult;
import com.zhenzi.sms.ZhenziSmsClient;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "zhenzisms.client")
@Component
@Data
public class SMSUtil {

	private String apiUrl;

	private String appId;

	private String appSecret;

	private String templateId;

	private String phoneRegex = "^(13[0-9]|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\\d{8}$";

	@Autowired
	RedisTemplate<String, Object> redisTemplate;

	public SMSResult sendMessage(String phoneNumber) throws Exception {
		if (!phoneNumber.matches(phoneRegex)) {
			return new SMSResult(20001, "不符合规范的手机号");
		}
		ZhenziSmsClient zhenziSmsClient = new ZhenziSmsClient(apiUrl, appId, appSecret);
		Map<String, Object> params = new HashMap<>();
		params.put("number", phoneNumber);
		params.put("templateId", templateId);
		String[] templateParams = new String[2];
		Random random = new Random();
		StringBuilder builder = new StringBuilder(6);
		for (int i = 0; i < 6; i++) {
			int num = random.nextInt(10);
			builder.append(num);
		}
		templateParams[0] = builder.toString();
		templateParams[1] = "5";
		params.put("templateParams", templateParams);
		String result = zhenziSmsClient.send(params);
		redisTemplate.opsForValue().set("authentication/" + phoneNumber, templateParams[0], Duration.ofSeconds(300L));
		return JSON.parseObject(result, SMSResult.class);
	}
}
