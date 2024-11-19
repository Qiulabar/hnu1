package client;

import com.zhenzi.sms.ZhenziSmsClient;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import org.junit.jupiter.api.Test;

public class ZhenZiYunTest {
	/**
	 * 使用榛子云发送短信
	 */
	@Test
	public void testSend() {
		String phoneNumber = "xxx";
		ZhenziSmsClient zhenziSmsClient = new ZhenziSmsClient("xxx",
				"xxx", "xxx");
		Map<String, Object> params = new HashMap<>();
		params.put("number", phoneNumber);
		params.put("templateId", "xxx");
		String[] templateParams = new String[2];
		Random random = new Random();
		StringBuilder builder = new StringBuilder(6);
		for (int i=0;i<6;i++) {
			int num = random.nextInt(10);
			builder.append(num);
		}
		templateParams[0] = builder.toString();
		templateParams[1] = "5";
		params.put("templateParams", templateParams);
		try {
			String result = zhenziSmsClient.send(params);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
