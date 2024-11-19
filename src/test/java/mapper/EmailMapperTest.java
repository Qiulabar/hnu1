package mapper;

import com.smtpmail.start.SmtpMailApplication;
import com.smtpmail.mapper.EmailMapper;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = SmtpMailApplication.class)
public class EmailMapperTest {
	@Autowired
	EmailMapper emailMapper;

	@Test
	public void test01() {
		List<String> list = Arrays.asList("11", "12", "13");
		emailMapper.removeBatch(list);
	}
}
