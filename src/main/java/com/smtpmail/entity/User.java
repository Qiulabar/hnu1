package com.smtpmail.entity;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * 邮件用户
 */
@Data
@TableName("user")
public class User {

	/**
	 * alias for email-address
	 */
	private String username;

	/**
	 * alias for authentication code
	 */
	private String password;

	private String phone;

	/**
	 * 用户等级
	 * 0：admin
	 * 1：普通用户
	 * -1：已禁用
	 */
	private Integer level;

	private String avatar;

	private Integer isPublisher;

	private Date createTime;

	/**
	 * 逻辑删除
	 */
	@TableLogic
	private Integer isDeleted;
}
