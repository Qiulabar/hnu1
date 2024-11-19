package com.smtpmail.mapper;

import com.smtpmail.entity.Email;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface EmailMapper {
	Email selectById(@Param("id") String id);

	List<Email> selectUnRead();

	List<Email> selectByFrom(@Param("mailFrom") String mailFrom);

	void insert(Email email);

	List<Email> selectByUsername(@Param("username") String username);

	/**
	 * 删除指定id的邮件，逻辑删除
	 */
	void removeBatch(@Param("idList") List<String> idList);

	/**
	 * 删除指定收件人邮箱内所有的邮件，物理删除
	 */
	void removeBatchByRcptTo(@Param("rcptToList") List<String> rcptToList);

	void updateState(@Param("id") String id,@Param("newState") int newState);
}
