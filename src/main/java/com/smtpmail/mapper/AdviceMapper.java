package com.smtpmail.mapper;

import com.smtpmail.entity.Advice;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AdviceMapper {

    @Insert("insert into advice(username,advice,create_time) values(#{username},#{advice},now())")
    int save(Advice advice);
}
