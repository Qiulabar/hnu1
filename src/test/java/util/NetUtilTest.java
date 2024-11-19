package util;

import com.smtpmail.start.SmtpMailApplication;
import com.smtpmail.util.NetUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = SmtpMailApplication.class)
public class NetUtilTest {
	@Autowired
	NetUtil netUtil;

	@Test
	public void test01() {
		System.out.println(netUtil.isPortUsing(30));
	}
}
