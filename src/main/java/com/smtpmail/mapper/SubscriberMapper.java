package com.smtpmail.mapper;

import com.smtpmail.entity.Subscriber;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SubscriberMapper {

	/**
	 * 查找所有订阅了publisher的subscribers
	 */
	List<String> findAllForPublisher(@Param("publisher") String publisher);

	void save(Subscriber subscriber);

	Subscriber select(Subscriber subscriber);

	void delete(Subscriber subscriber);

	List<String> findAllForSubscriber(@Param("subscriber") String username);
}
