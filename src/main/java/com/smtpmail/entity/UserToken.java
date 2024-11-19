package com.smtpmail.entity;

import com.alibaba.fastjson.JSON;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用于Token中的JSON转换
 */
@Data
@NoArgsConstructor
public class UserToken {
	private String username;

	private Integer level;

	private String avatar;

	private Integer isPublisher;

	public UserToken(User u) {
		if (u == null) {
			this.username = null;
			this.level = null;
			this.avatar = null;
			this.isPublisher = null;
		} else {
			this.username = u.getUsername();
			this.level = u.getLevel();
			this.avatar = u.getAvatar();
			this.isPublisher = u.getIsPublisher();
		}
	}

	public String toJSON() {
		return JSON.toJSONString(this);
	}
}
