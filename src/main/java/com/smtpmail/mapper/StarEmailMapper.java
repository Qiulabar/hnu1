package com.smtpmail.mapper;

import com.smtpmail.entity.StarEmail;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface StarEmailMapper {

	void insert(StarEmail starEmail);

	StarEmail selectById(@Param("id") String emailId);

	void deleteById(String emailId);

	List<StarEmail> list(@Param("belong_user") String username);
}
