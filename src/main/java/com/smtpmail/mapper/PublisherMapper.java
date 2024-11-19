package com.smtpmail.mapper;

import com.smtpmail.entity.Publisher;
import com.smtpmail.entity.PublisherShow;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface PublisherMapper {

	void save(Publisher publisher);

	void updateAudit(Publisher p);

	long selectAudit0Count();

	long selectAudit1Count();

	List<Publisher> selectAudit0(@Param("current") Long current, @Param("size") Long size);

	/**
	 * 获取所有publisher列表：for manager
	 */
	List<Publisher> selectAudit1(@Param("current") Long current, @Param("size") Long size);

	/**
	 * 获取所有publisher列表：for common user
	 */
	List<Publisher> selectAudit1ForUser();

	Publisher select(@Param("username") String username);

	/**
	 * 分页模糊查询计数
	 */
	Long selectFuzzyCount(@Param("username") String username, @Param("state") Integer state);

	List<Publisher> selectList(@Param("publishers") List<String> publishers);

	void removeBatch(@Param("nameList") List<String> nameList);

	void updateAvatar(@Param("username") String username, @Param("avatar") String url);

	List<PublisherShow> selectFuzzy(@Param("username") String username, @Param("state") Integer state, @Param("current") Long current, @Param("limit") Long limit);
}
