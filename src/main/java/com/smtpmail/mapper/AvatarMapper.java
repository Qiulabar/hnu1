package com.smtpmail.mapper;

import com.smtpmail.entity.Avatar;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AvatarMapper {

	void save(Avatar avatar);

	List<Avatar> findAllByUsername(@Param("username") String username);

	void remove(@Param("username") String username,@Param("avatarList") List<Avatar> avatarList);
}
