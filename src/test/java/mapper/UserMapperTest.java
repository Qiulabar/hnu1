package mapper;

import com.smtpmail.start.SmtpMailApplication;
import com.smtpmail.entity.User;
import com.smtpmail.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = SmtpMailApplication.class)
public class UserMapperTest {
	@Autowired
	UserMapper userMapper;

	@Test
	public void test01() {
		User user = new User();
		user.setUsername("1462749490@qq.com");
		user.setPassword("chenwenkang");
		userMapper.insert(user);
	}
}
