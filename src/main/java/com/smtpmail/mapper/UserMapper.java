package com.smtpmail.mapper;

import com.smtpmail.entity.User;
import com.smtpmail.entity.UserShow;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {

	/**
	 * 获取所有用户，包括管理员
	 */
	List<User> selectAll();

	Long pageAllCount();

	List<User> pageAll(@Param("current") Long current, @Param("limit") Long limit);

	List<User> selectByLevel(@Param("level") Integer level);

	User select(@Param("username") String username);

	/**
	 * 模糊分页查询用户
	 */
	List<UserShow> selectFuzzy(@Param("username") String username, @Param("current") Long current, @Param("limit") Long limit);

	/**
	 * 模糊查询计数
	 */
	Long selectFuzzyCount(@Param("username") String username);

	int selectAuth(@Param("username") String username, @Param("password") String password);

	/**
	 * 查找数据库中是否有username记录，包括is_deleted=1
	 */
	User selectExist(@Param("username") String username);

	void insert(User user);

	void remove(@Param("username") String username);

	/**
	 * 根据username更新
	 */
	void update(User user);

	/**
	 * 批量禁用用户
	 */
	void disableBatch(@Param("nameList") List<String> nameList);

	/**
	 * 批量开启用户
	 */
	void ableBatch(@Param("nameList") List<String> nameList);

	/**
	 * 批量删除用户
	 */
	void removeBatch(@Param("nameList") List<String> nameList);

	/**
	 * 保存用户头像
	 */
	void saveAvatar(@Param("username") String username, @Param("avatar") String avatar);

	void updatePublisher(@Param("username") String username, @Param("state") Integer result);

	/**
	 * 批量更新用户的isPublisher字段
	 */
	void updatePublisherBatch(@Param("nameList") List<String> nameList, @Param("state") int i);

	/**
	 * 统计两周内的用户注册数量
	 * timeDiff作为key
	 */
	@MapKey("timeDiff")
	Map<Long, Map<String, Long>> statistics();


}
