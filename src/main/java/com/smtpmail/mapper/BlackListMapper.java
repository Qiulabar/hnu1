package com.smtpmail.mapper;

import com.smtpmail.entity.BlackList;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface BlackListMapper {

	List<BlackList> listBlackList(@Param("username") String receiver);

	void insert(BlackList blackList);

	int selectExist(BlackList blackList);

	void removeBatch(@Param("idList") List<Long> idList,@Param("username") String username);

}
