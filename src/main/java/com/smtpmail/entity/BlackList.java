package com.smtpmail.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.smtpmail.common.DeleteGroup;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Pattern;
import lombok.Data;

@Data
@TableName("black_list")
public class BlackList {
	@TableId(type = IdType.AUTO)
	@Null
	@NotNull(groups = DeleteGroup.class)
	Long id;

	/**
	 * 冗余字段：用户名
	 */
	@NotNull
	@Pattern(regexp = "^[a-zA-Z0-9]+@[a-zA-Z0-9]+\\.[a-zA-Z0-9]+$", message = "用户名应保持邮箱格式")
	String username;

	/**
	 * 黑名单目标用户username或ip
	 */
	@NotNull
	String blackTarget;

	/**
	 * 根据用户名加入黑名单或ip加入黑名单
	 * 0: username
	 * 1: ip
	 */
	@NotNull
	@Min(value = 0, message = "取值范围在[0,1]")
	@Max(value = 1, message = "取值范围在[0,1]")
	Integer type;
}
