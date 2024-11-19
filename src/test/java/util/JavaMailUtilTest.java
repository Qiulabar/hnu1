package util;

import com.smtpmail.util.JavaMailUtil;
import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import org.junit.jupiter.api.Test;

public class JavaMailUtilTest {
	@Test
	public void test01() {
		final String fromEmail = "1462749490@lunangangster.store"; //requires valid gmail id
		final String password = "1462749490@lunangangster.store"; // correct password for gmail id
		final String toEmail = "susu@lunangangster.store"; // can be any email id

		System.out.println("TLSEmail Start");
		Properties props = new Properties();
		props.put("mail.smtp.host", "localhost"); //SMTP Host
		props.put("mail.smtp.port", "25"); //TLS Port：587
		props.put("mail.smtp.auth", "true"); //enable authentication
		props.put("mail.smtp.starttls.enable", "false"); //enable STARTTLS

		//create Authenticator object to pass in Session.getInstance argument
		Authenticator auth = new Authenticator() {
			//override the getPasswordAuthentication method
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(fromEmail, password);
			}
		};
		// 创建邮件会话
		Session session = Session.getInstance(props, auth);

		JavaMailUtil.sendEmail(session, fromEmail, toEmail,"TLSEmail Testing Subject", "TLSEmail Testing Body");
	}
}
