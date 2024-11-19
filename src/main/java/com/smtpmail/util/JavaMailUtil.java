package com.smtpmail.util;

import java.util.Date;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class JavaMailUtil {

	public static void sendEmail(Session session, String from, String to, String subject, String body) {
		try {
			// 邮件对象
			MimeMessage msg = new MimeMessage(session);
			//set message headers
			msg.addHeader("Content-type", "text/HTML; charset=UTF-8");
			msg.addHeader("format", "flowed");
			msg.addHeader("Content-Transfer-Encoding", "8bit");
			msg.setFrom(from);
			msg.setReplyTo(InternetAddress.parse(to, false));

			// 设置邮件头
			msg.setSubject(subject, "UTF-8");

			// 设置邮件内容
			msg.setText(body, "UTF-8");

			// 设置发送时间
			msg.setSentDate(new Date());

			// 设置收件人
			msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to, false));
			System.out.println("Message is ready");
			// 发送邮件
			Transport.send(msg);

			System.out.println("EMail Sent Successfully!!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void sendEmailWithAttachment(Session session, String from, String to, String subject,
			String body, String attachmentFilePath) {
		if (attachmentFilePath == null) {
			throw new NullPointerException("附件路径不能为空");
		}
		try {
			MimeMessage msg = new MimeMessage(session);
			msg.addHeader("Content-type", "text/HTML; charset=UTF-8");
			msg.addHeader("format", "flowed");
			msg.addHeader("Content-Transfer-Encoding", "8bit");

			msg.setFrom(from);

			msg.setReplyTo(InternetAddress.parse(to, false));

			msg.setSubject(subject, "UTF-8");

			msg.setSentDate(new Date());

			msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to, false));

			// Create the message body part
			BodyPart messageBodyPart = new MimeBodyPart();

			// Fill the message
			messageBodyPart.setText(body);

			// Create a multipart message for attachment
			Multipart multipart = new MimeMultipart();

			// Second part is attachment
			messageBodyPart = new MimeBodyPart();

			// 下方是文件路径样例
			// String filePath = "C:/Users/DELL/Desktop/转出院系学生调查.docx";

			// 新建文件源
			DataSource source = new FileDataSource(attachmentFilePath);

			// 设置数据解析器
			messageBodyPart.setDataHandler(new DataHandler(source));

			// 设置附件文件名
			messageBodyPart.setFileName(source.getName());
			multipart.addBodyPart(messageBodyPart);

			// Send the complete message parts
			msg.setContent(multipart);

			// Send message
			Transport.send(msg);
			System.out.println("EMail Sent Successfully with attachment!!");
		} catch (MessagingException e) {
			e.printStackTrace();
			System.out.println("EMail Sent Fail with attachment!!");
		}
	}
}
