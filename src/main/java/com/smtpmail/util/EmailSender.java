package com.smtpmail.util;

import com.smtpmail.entity.Email;
import com.smtpmail.entity.User;
import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;

public class EmailSender {
	private static final String HOST = "127.0.0.1";
	private static final Integer PORT = 25;


	public static void sendEmail(User user, Email email) {
		Properties props = new Properties();
		props.put("mail.smtp.host", HOST);
		props.put("mail.smtp.port", PORT);
		props.put("mail.smtp.auth", "true"); //enable authentication
		props.put("mail.smtp.starttls.enable", "false"); //enable STARTTLS

		String fromEmail = email.getMailFrom();
		String password = user.getPassword();
		// create Authenticator object to pass in Session.getInstance argument
		Authenticator auth = new Authenticator() {
			//override the getPasswordAuthentication method
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(fromEmail, password);
			}
		};
		// 创建邮件会话
		Session session = Session.getInstance(props, auth);

		JavaMailUtil.sendEmail(session, email.getMailFrom(), email.getRcptTo(), email.getSubject(), email.getDatagram());
	}
}
