package util;

import com.smtpmail.entity.Email;
import com.smtpmail.entity.User;
import com.smtpmail.util.EmailSender;
import java.util.Date;
import org.junit.jupiter.api.Test;

public class EmailSenderTest {
	@Test
	public void test01() {
		User user = new User();
		user.setUsername("1462749490@lunangangster.store");
		user.setPassword("1462749490@lunangangster.store");
		Email email = new Email();
		email.setRcptTo("susu@lunangangster.store");
		email.setDate(new Date());
		email.setMailFrom("1462749490@lunangangster.store");
		email.setSubject("Hello");
		email.setData("Hello World");
		email.setDatagram(email.getData());
		EmailSender.sendEmail(user, email);
	}
}
