package com.smtpmail.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

@Data
@TableName("email")
public class Email {
	@TableId(type = IdType.INPUT)
	private String id;
	private String ip;
	private String helo;
	private String mailFrom;
	private String rcptTo;
	private String from;
	private String to;
	private String subject;
	private Date date;
	private String data;
	private String datagram;
	private Integer length;
	private Integer state;
	@TableLogic
	private Integer isDeleted;

	@Override
	public String toString() {
		return "Email{" +
				"id='" + id + '\'' +
				", ip='" + ip + '\'' +
				", helo='" + helo + '\'' +
				", mailFrom='" + mailFrom + '\'' +
				", rcptTo='" + rcptTo + '\'' +
				", from='" + from + '\'' +
				", to='" + to + '\'' +
				", subject='" + subject + '\'' +
				", date=" + date +
				", data='" + data + '\'' +
				", datagram='" + datagram + '\'' +
				", length=" + length +
				", state=" + state +
				", isDeleted=" + isDeleted +
				'}';
	}
}
