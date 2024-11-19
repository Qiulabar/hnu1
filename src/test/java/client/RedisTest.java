package client;

import com.smtpmail.start.SmtpMailApplication;
import java.time.Duration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

@SpringBootTest(classes = SmtpMailApplication.class)
public class RedisTest {
	@Autowired
	RedisTemplate<String, Object> redisTemplate;

	@Test
	public void test01() {
		redisTemplate.opsForValue().set("authorization", 124122, Duration.ofSeconds(60L));
	}
}
