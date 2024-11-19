package util;

import com.smtpmail.start.SmtpMailApplication;
import com.smtpmail.util.LogFileUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = SmtpMailApplication.class)
public class LogFileUtilTest {
	@Autowired
	LogFileUtil logFileUtil;

	@Test
	public void test01 () {
		System.out.println(logFileUtil.getFilesNameFromDirectory(logFileUtil.infoFilePath));
	}

}
