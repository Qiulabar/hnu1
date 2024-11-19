package com.smtpmail.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smtpmail.common.Result;
import com.smtpmail.entity.Email;
import com.smtpmail.entity.Publisher;
import com.smtpmail.entity.PublisherShow;
import com.smtpmail.entity.User;
import com.smtpmail.entity.UserToken;
import com.smtpmail.mapper.PublisherMapper;
import com.smtpmail.mapper.SubscriberMapper;
import com.smtpmail.mapper.UserMapper;
import com.smtpmail.util.EmailSender;
import com.smtpmail.util.JWTUtil;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/smtpmail/publisher")
@Transactional
@Slf4j
public class PublisherController {

	@Autowired
	PublisherMapper publisherMapper;
	@Autowired
	UserMapper userMapper;
	@Autowired
	SubscriberMapper subscriberMapper;
	@Autowired
	ExecutorService threadPool;
	@Value("${official.mail}")
	String officialMail;
	@Value("${official.password}")
	String officialPassword;
	@Autowired
	RedisTemplate<String, Object> redisTemplate;
	@Autowired
	JWTUtil jwtUtil;

	/**
	 * 发布邮件给所有订阅者 Email: subject rcptTo datagram
	 */
	@PostMapping
	public Result publish(@RequestBody Email email) {
		Integer isPublisher = jwtUtil.getUserToken().getIsPublisher();
		if (isPublisher != 1) {
			return Result.error().message("非publisher");
		}
		String username = jwtUtil.getUserToken().getUsername();
		User publisher = userMapper.select(username);
		List<String> subscribers = subscriberMapper.findAllForPublisher(username);
		for (String subscriber : subscribers) {
			threadPool.execute(() -> {
				Email e = new Email();
				e.setSubject(email.getSubject());
				e.setMailFrom(username);
				e.setRcptTo(subscriber);
				e.setDatagram(email.getDatagram());
				e.setDate(new Date());
				EmailSender.sendEmail(publisher, e);
			});
		}
		return Result.ok();
	}

	/**
	 * 申请认证publisher
	 */
	@GetMapping("/request")
	public Result certify() {
		UserToken userToken = jwtUtil.getUserToken();
		Integer isPublisher = userToken.getIsPublisher();
		if (isPublisher == 1) {
			return Result.error().message("已经是publisher，无需重复注册");
		}
		String username = userToken.getUsername();
		log.info("【{}】用户尝试申请publisher", username);
		Publisher publisher = publisherMapper.select(username);
		if (publisher != null && publisher.getState() != -1) {
			log.info("【{}】用户尝试申请publisher，申请失败：已通过申请或已在申请列表中", username);
			return Result.error().message("申请失败：已通过申请或已在申请列表中");
		}
		Publisher p = new Publisher(username, 0);
		p.setAvatar(userToken.getAvatar());
		p.setAckTime(new Date());
		if (publisher == null) {
			publisherMapper.save(p);
		} else {
			publisherMapper.updateAudit(p);
		}
		log.info("【{}】用户申请publisher成功，等待管理员审核", username);
		return Result.ok().message("申请成功，等待管理员审核");
	}

	/**
	 * 用户获取申请状态 可以申请：-1（被拒绝，未申请过） 审核中：0 已通过：1
	 */
	@GetMapping("/auditing/state")
	public Result getAudit() {
		UserToken userToken = jwtUtil.getUserToken();
		Integer isPublisher = userToken.getIsPublisher();
		// 用户已经是publisher
		if (isPublisher == 1) {
			return Result.ok().data("state", 1).message("申请成功");
		}
		Publisher publisher = publisherMapper.select(userToken.getUsername());
		// 用户未申请过
		if (publisher == null || publisher.getState() == -1) {
			return Result.ok().data("state", -1).message("可以申请");
		}
		// 审核中
		return Result.ok().data("state", 0).message("审核中");
	}

	/**
	 * 管理员接口：获取申请列表
	 */
	@GetMapping("/auditing/{current}/{size}")
	public Result getAuditList(@PathVariable("current") Long current,
			@PathVariable("size") Long size) {
		String managerName = jwtUtil.getUserToken().getUsername();
		if (!jwtUtil.isManager()) {
			log.error("【{}】用户尝试审核publisher申请，失败原因：无权限", managerName);
			return Result.error().message("无权限");
		}
		long count = publisherMapper.selectAudit0Count();
		List<Publisher> list = publisherMapper.selectAudit0((current - 1) * size, size);
		Page<Publisher> page = new Page<>(current, size);
		page.setRecords(list).setTotal(count);
		return Result.ok().data("page", page);
	}

	/**
	 * 管理员接口：获取申请成功列表
	 */
	@GetMapping("/audited/{current}/{size}")
	public Result getAudited(@PathVariable("current") Long current, @PathVariable("size") Long size) {
		String managerName = jwtUtil.getUserToken().getUsername();
		if (!jwtUtil.isManager()) {
			log.error("【{}】用户尝试审核publisher申请，失败原因：无权限", managerName);
			return Result.error().message("无权限");
		}
		long count = publisherMapper.selectAudit1Count();
		List<Publisher> list = publisherMapper.selectAudit1((current - 1) * size, size);
		Page<Publisher> page = new Page<>(current, size);
		page.setRecords(list).setTotal(count);
		return Result.ok().data("page", page);
	}

	/**
	 * 获取当前subscriber的所有订阅publisher，以及所有publisher
	 */
	@GetMapping("/audited")
	public Result getMyAuditList() {
		User user = userMapper.select(jwtUtil.getUserToken().getUsername());
		if (user == null) {
			return Result.error().message("获取失败");
		}
		List<String> publishers = subscriberMapper.findAllForSubscriber(user.getUsername());
		List<Publisher> list = publisherMapper.selectAudit1ForUser();
		return Result.ok().data("myPublisher", publishers).data("publisherList", list);
	}

	// /**
	//  * 获取当前subscriber的所有订阅publisher，以及所有publisher
	//  */
	// @GetMapping("/audited")
	// public Result getMyAuditList() {
	// 	User user = userMapper.select(jwtUtil.getUserToken().getUsername());
	// 	if (user == null) {
	// 		return Result.error().message("获取失败");
	// 	}
	// 	CompletableFuture<List<Publisher>> f1 = CompletableFuture.supplyAsync(() -> {
	// 		List<String> publishers = subscriberMapper.findAllForSubscriber(user.getUsername());
	// 		return publisherMapper.selectList(publishers);
	// 	}, threadPool);
	// 	CompletableFuture<List<Publisher>> f2 = CompletableFuture.supplyAsync(() -> {
	// 		return publisherMapper.selectAudit1ForUser();
	// 	}, threadPool);
	// 	List<Publisher> myPublisher = null;
	// 	List<Publisher> publisherList = null;
	// 	try {
	// 		CompletableFuture.allOf(f1, f2).get();
	// 		myPublisher = f1.get();
	// 		publisherList = f2.get();
	// 	} catch (InterruptedException | ExecutionException e) {
	// 		return Result.error();
	// 	}
	// 	return Result.ok().data("myPublisher", myPublisher).data("publisherList", publisherList);
	// }

	/**
	 * 管理员接口：根据用户名查找publisher
	 */
	@GetMapping("/search/{state}/{current}/{limit}")
	public Result searchPublisher(String username,
			@PathVariable("state") Integer state,
			@PathVariable("current") Long current,
			@PathVariable("limit") Long limit) {
		if (!jwtUtil.isManager()) {
			log.error("【{}】用户尝试获取所有管理员，失败原因：无权限", jwtUtil.getUserToken().getUsername());
			return Result.error().message("无权限");
		}
		if (!StringUtils.hasText(username)) {
			username = "";
		}
		username = "%" + username + "%@lunangangster.store";
		String finalUsername = username;
		CompletableFuture<Long> f1 = CompletableFuture.supplyAsync(() -> {
			return publisherMapper.selectFuzzyCount(finalUsername, state);
		}, threadPool);
		CompletableFuture<List<PublisherShow>> f2 = CompletableFuture.supplyAsync(() -> {
			return publisherMapper.selectFuzzy(finalUsername, state, (current - 1) * limit, limit);
		}, threadPool);
		try {
			CompletableFuture.allOf(f1, f2).get();
			Page<PublisherShow> page = new Page<>(current, limit);
			page.setTotal(f1.get()).setRecords(f2.get());
			return Result.ok().data("page", page);
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
			return Result.error();
		}
	}

	/**
	 * 获取所有publisher列表
	 */
	@GetMapping("/audited/user")
	public Result getAuditedByUser() {
		List<Publisher> list = publisherMapper.selectAudit1ForUser();
		return Result.ok().data("list", list);
	}

	/**
	 * 管理员接口：审核publisher申请 result: -1: 拒绝 0：审核中 1：已通过
	 */
	@GetMapping("/audit/{result}")
	public Result audit(@PathVariable("result") Integer result, @RequestParam String username) {
		String managerName = jwtUtil.getUserToken().getUsername();
		if (!jwtUtil.isManager()) {
			log.error("【{}】用户尝试审核publisher申请，失败原因：无权限", managerName);
			return Result.error().message("无权限");
		}
		if (result != -1 && result != 1) {
			return Result.error().message("审核失败：参数错误：result must in (-1, 1)");
		}
		Publisher p = new Publisher();
		p.setUsername(username);
		p.setAuditTime(new Date());
		p.setState(result);
		log.info("【{}】用户审核了【{}】的publisher申请：审核【{}】", managerName, username, result == 1 ? "通过" : "不通过");
		// 异步编排
		CompletableFuture<Void> f1 = CompletableFuture.runAsync(() -> {
			publisherMapper.updateAudit(p);
		}, threadPool);
		CompletableFuture<Void> f2 = CompletableFuture.runAsync(() -> {
			userMapper.updatePublisher(username, result);
		}, threadPool);
		CompletableFuture<Void> f3 = CompletableFuture.runAsync(() -> {
			User user = new User();
			user.setUsername(officialMail);
			user.setPassword(officialPassword);
			Email email = new Email();
			email.setMailFrom(officialMail);
			email.setRcptTo(username);
			email.setFrom(officialMail);
			email.setTo(username);
			email.setDate(new Date());
			email.setSubject("publisher申请审核结果");
			if (result == 1) {
				email.setDatagram("恭喜您申请成为publisher成功！");
			} else {
				email.setDatagram("很遗憾，您申请publisher失败");
			}
			EmailSender.sendEmail(user, email);
		}, threadPool);
		try {
			// 阻塞等待所有任务完成
			CompletableFuture.allOf(f1, f2, f3).get();
			return Result.ok().message("审核完成");
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
			return Result.error().message("审核过程出错");
		}
	}

	/**
	 * 管理员接口：取消publisher资格
	 */
	@DeleteMapping
	public Result cancelPublisher(@RequestBody List<String> nameList) {
		String managerName = jwtUtil.getUserToken().getUsername();
		if (!jwtUtil.isManager()) {
			log.error("【{}】用户尝试取消publisher资格，失败原因：无权限", managerName);
			return Result.error().message("无权限");
		}
		CompletableFuture<Void> f1 = CompletableFuture.runAsync(() -> {
			publisherMapper.removeBatch(nameList);
		}, threadPool);
		CompletableFuture<Void> f2 = CompletableFuture.runAsync(() -> {
			userMapper.updatePublisherBatch(nameList, 0);
		}, threadPool);
		try {
			CompletableFuture.allOf(f1, f2).get();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
			return Result.error().message("取消失败");
		}
		log.info("【{}】用户取消【{}】名publisher资格，成功", managerName, nameList.size());
		for (String s : nameList) {
			log.info("【{}】的publisher资格被取消", s);
		}
		return Result.ok();
	}

}
