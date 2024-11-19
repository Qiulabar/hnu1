package com.smtpmail.entity;

import java.util.Date;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

/**
 * 给官方的建议
 */
@Data
@NoArgsConstructor
public class Advice {
	private String username;

	private String advice;

	private Date createTime;

	public Advice(String username, String advice) {
		this.username = username;
		this.advice = advice;
		this.createTime = new Date();
	}

    @Mapper
    public static interface AdviceMapper {

        @Insert("insert into advice(advice,username,advice.create_time) value ()")
        void save(Advice advice);
    }
}
