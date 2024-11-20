package com.smtpmail.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smtpmail.common.DeleteGroup;
import com.smtpmail.common.Result;
import com.smtpmail.entity.Avatar;
import com.smtpmail.entity.BlackList;
import com.smtpmail.entity.Email;
import com.smtpmail.entity.Entry;
import com.smtpmail.entity.SMSResult;
import com.smtpmail.entity.StarEmail;
import com.smtpmail.entity.User;
import com.smtpmail.entity.UserRegister;
import com.smtpmail.entity.UserShow;
import com.smtpmail.entity.UserToken;
import com.smtpmail.entity.UserUpdate;
import com.smtpmail.mapper.AvatarMapper;
import com.smtpmail.mapper.BlackListMapper;
import com.smtpmail.mapper.EmailMapper;
import com.smtpmail.mapper.PublisherMapper;
import com.smtpmail.mapper.StarEmailMapper;
import com.smtpmail.mapper.UserMapper;
import com.smtpmail.service.EmailService;
import com.smtpmail.service.UserService;
import com.smtpmail.util.JWTUtil;
import com.smtpmail.util.OSSUtil;
import com.smtpmail.util.SMSUtil;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@Transactional
@RequestMapping("/smtpmail/user")
@Slf4j
public class UserController {

	@Autowired
	UserService userService;
	@Autowired
	EmailService emailService;
	@Autowired
	EmailMapper emailMapper;
	@Autowired
	BlackListMapper blackListMapper;
	@Autowired
	StarEmailMapper starEmailMapper;
	@Autowired
	AvatarMapper avatarMapper;
	@Autowired
	PublisherMapper publisherMapper;
	@Autowired
	RedisTemplate<String, Object> redisTemplate;
	@Autowired
	HttpServletResponse response;
	@Autowired
	ExecutorService threadPool;
	@Autowired
	JWTUtil jwtUtil;
	@Autowired
	SMSUtil smsUtil;
	@Autowired
	OSSUtil ossUtil;

	/**
	 * 获取当前登录用户的信息
	 */
	@GetMapping("/token")
	public Result getCurrentUser() {
		UserToken userToken = jwtUtil.getUserToken();
		return Result.ok().data("user", userToken);
	}

	/**
	 * 管理员接口：获取所有用户，包括管理员
	 */
	@GetMapping("/all/{current}/{limit}")
	public Result getUser(@PathVariable("current") Long current, @PathVariable("limit") Long limit) {
		if (!jwtUtil.isManager()) {
			log.error("【{}】用户尝试获取所有管理员，失败原因：无权限", jwtUtil.getUserToken().getUsername());
			return Result.error().message("无权限");
		}
		Long count = userService.pageAllCount();
		List<User> users = userService.pageAll(current, limit);
		Page<User> page = new Page<>(current, limit);
		page.setTotal(count).setRecords(users);
		log.info("【{}】成功获取所有用户列表(size={})", jwtUtil.getUserToken().getUsername(), count);
		return Result.ok().data("page", page);
	}

	/**
	 * 管理员接口：根据用户名查找用户
	 */
	@GetMapping("/search/{current}/{limit}")
	public Result searchUser(String username, @PathVariable("current") Long current,
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
			return userService.selectFuzzyCount(finalUsername);
		}, threadPool);
		CompletableFuture<List<UserShow>> f2 = CompletableFuture.supplyAsync(() -> {
			return userService.selectFuzzy(finalUsername, current, limit);
		}, threadPool);
		try {
			CompletableFuture.allOf(f1, f2).get();
			Page<UserShow> page = new Page<>(current, limit);
			page.setTotal(f1.get()).setRecords(f2.get());
			return Result.ok().data("page", page);
		} catch (InterruptedException | ExecutionException e) {
			return Result.error();
		}
	}

	/**
	 * 管理员接口：获取所有管理员
	 */
	@GetMapping("/all/manager")
	public Result getManagers() {
		if (!jwtUtil.isManager()) {
			log.error("【{}】用户尝试获取所有管理员，失败原因：无权限", jwtUtil.getUserToken().getUsername());
			return Result.error().message("无权限");
		}
		log.info("【{}】成功获取管理员列表", jwtUtil.getUserToken().getUsername());
		List<User> users = userService.selectByLevel(1);
		return Result.ok().data("users", users);
	}

	/**
	 * 用户登录
	 */
	@PostMapping("/login")
	public Result login(@RequestBody User user) {
		response.setHeader(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, JWTUtil.TOKEN_KEY);
		User u; // 数据库对象
		if (user == null || user.getUsername() == null) {
			return Result.error().message("用户信息不完整");
		} else {
			String errorMessage = "";
			u = userService.select(user.getUsername());
			if (u == null) {
				errorMessage = "用户不存在";
			} else if (u.getLevel() == -1) {
				errorMessage = "用户已被禁用";
			} else if (!u.getPassword().equals(user.getPassword())) {
				errorMessage = "用户密码错误";
			}
			if (StringUtils.hasText(errorMessage)) {
				if (u != null) {
					log.error(u.getUsername() + "登录失败，原因：【" + errorMessage + "】");
				}
				return Result.error().message(errorMessage);
			}
		}
		// JWT获取token，并保存
		assert u != null;
		String token = JWTUtil.createToken(new UserToken(u).toJSON());
		response.setHeader(JWTUtil.TOKEN_KEY, token);
		// 设置cookie到客户机
		Cookie cookie = new Cookie(JWTUtil.TOKEN_KEY, token);
		cookie.setPath("/");
		cookie.setMaxAge(2880);
		response.addCookie(cookie);
		log.info(u.getUsername() + "成功登录");
		return Result.ok().message("登录成功");
	}

	/**
	 * 管理员接口：管理员登录
	 */
	@PostMapping("/manager/login")
	public Result managerLogin(@RequestBody User user) {
		response.setHeader(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, JWTUtil.TOKEN_KEY);
		if (!StringUtils.hasText(user.getUsername())
				|| !StringUtils.hasText(user.getPassword())) {
			return Result.error().message("用户名或密码错误");
		}
		// JWT获取token，并保存
		User u = userService.select(user.getUsername());
		if (!u.getPassword().equals(user.getPassword())
				|| u.getLevel() != 0) {
			return Result.error().message("用户名或密码错误");
		}
		String token = JWTUtil.createToken(new UserToken(u).toJSON());
		response.setHeader(JWTUtil.TOKEN_KEY, token);
		log.info("【{}】登录管理系统", u.getUsername());
		return Result.ok().message("登录成功");
	}

	/**
	 * 用户注册
	 */
	@PostMapping("/register")
	public Result register(@RequestBody UserRegister userRegister) {
		if (!StringUtils.hasText(userRegister.getUsername())
				|| !StringUtils.hasText(userRegister.getPassword())
				|| !StringUtils.hasText(userRegister.getPhone())) {
			return Result.error().message("注册失败：用户信息不完整");
		}
		// TODO Redis验证授权码
		String code = (String) redisTemplate.opsForValue()
				.get("authentication/" + userRegister.getPhone());
		System.out.println("正确码：" + code);
		System.out.println("输入码：" + userRegister.getPhone());
		if (code == null || !code.equals(userRegister.getAuthCode())) {
			return Result.error().message("验证码错误");
		}
		User u = userService.selectExist(userRegister.getUsername());
		User user = new User();
		BeanUtils.copyProperties(userRegister, user);
		user.setLevel(1);
		user.setIsDeleted(0);
		user.setCreateTime(new Date());
		if (u == null) {  // 如果该用户名没有被注册过，则直接插入
			userService.insert(user);
			log.info("【{}】用户注册系统成功", user.getUsername());
			return Result.ok().message("用户注册成功");
		} else if (u.getIsDeleted() == 1) { // 如果该用户名被注册过，则清空数据再进行更新操作
			// 删除原先用户的所有接收邮件 TODO 异步删除
			emailService.removeBatchByRcptTo(user.getUsername());
			// 更新用户
			userService.update(user);
			log.info("【{}】用户注册系统成功", user.getUsername());
			return Result.ok().message("用户注册成功");
		}
		log.error("【{}】用户已被注册", user.getUsername());
		return Result.error().message("注册失败：该邮箱已被注册");
	}

	/**
	 * 管理员接口：管理员禁用用户
	 */
	@PostMapping("/disable")
	public Result disableUser(@RequestBody List<String> nameList) {
		if (!jwtUtil.isManager()) {
			log.error("【{}】用户尝试禁用用户，失败原因：无权限", jwtUtil.getUserToken().getUsername());
			return Result.error().message("无权限");
		}
		if (!nameList.isEmpty()) {
			String managerName = jwtUtil.getUserToken().getUsername();
			log.info("【{}】禁用{}名用户成功", managerName, nameList.size());
			for (String name : nameList) {
				log.info("【{}】管理员禁用【{}】", managerName, name);
			}
			userService.disableBatch(nameList);
		}
		return Result.ok();
	}

	/**
	 * 管理员接口：管理员开启用户
	 */
	@PostMapping("/able")
	public Result ableUser(@RequestBody List<String> nameList) {
		if (!jwtUtil.isManager()) {
			log.error("【{}】用户尝试解禁用户，失败原因：无权限", jwtUtil.getUserToken().getUsername());
			return Result.error().message("无权限");
		}
		if (!nameList.isEmpty()) {
			String managerName = jwtUtil.getUserToken().getUsername();
			log.info("【{}】解禁{}名用户成功", managerName, nameList.size());
			for (String name : nameList) {
				log.info("【{}】管理员解禁【{}】", managerName, name);
			}
			userService.ableBatch(nameList);
		}
		return Result.ok();
	}

	/**
	 * 获取验证码
	 */
	@GetMapping("/phoneAuth")
	public Result getAuthCode(String phoneNumber) {
		try {
			SMSResult smsResult = smsUtil.sendMessage(phoneNumber);
			if (smsResult.getCode() == 20001) {
				return Result.error().message(smsResult.getData());
			}
			if (smsResult.getCode() == 0) {
				return Result.ok().message("短信发送成功");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Result.error().message("榛子云发送短信异常");

		// TODO 调用短信发送验证码
		// return Result.ok().data("code", "111111");
	}

	/**
	 * 用户修改密码或手机
	 */
	@PutMapping("/update")
	public Result updateUserPhone(@RequestBody UserUpdate user) {
		User u = userService.select(user.getUsername());
		if (u == null || u.getLevel() == -1) {
			return Result.error().message("用户不存在或已被禁用");
		}
		if (!u.getPassword().equals(user.getPassword())) {
			log.error("【{}】用户修改信息失败，原因：旧密码错误", u.getUsername());
			return Result.error().message("旧密码错误");
		}
		if (!StringUtils.hasText(user.getNewPhone()) && !StringUtils.hasText(user.getNewPassword())) {
			log.error("【{}】用户修改信息失败，原因：无信息输入", u.getUsername());
			return Result.error().message("无信息输入");
		}
		if (StringUtils.hasText(user.getNewPhone())) {
			u.setPhone(user.getNewPhone());
		}
		if (StringUtils.hasText(user.getNewPassword())) {
			u.setPassword(user.getNewPassword());
		}
		userService.update(u);
		log.info("【{}】用户修改信息成功", u.getUsername());
		return Result.ok();
	}

	/**
	 * 管理员接口：删除用户
	 */
	@DeleteMapping
	public Result removeUser(@RequestBody List<String> nameList) {
		if (!jwtUtil.isManager()) {
			log.error("【{}】用户尝试删除用户，失败原因：无权限", jwtUtil.getUserToken().getUsername());
			return Result.error().message("无权限");
		}
		if (!nameList.isEmpty()) {
			String managerName = jwtUtil.getUserToken().getUsername();
			log.info("【{}】删除{}名用户成功", managerName, nameList.size());
			for (String name : nameList) {
				log.info("【{}】管理员删除【{}】", managerName, name);
			}
			CompletableFuture<Void> f1 = CompletableFuture.runAsync(() -> {
				userService.removeBatch(nameList);
			}, threadPool);
			CompletableFuture<Void> f2 = CompletableFuture.runAsync(() -> {
				publisherMapper.removeBatch(nameList);
			}, threadPool);
			CompletableFuture<Void> f3 = CompletableFuture.runAsync(() -> {
				emailMapper.removeBatchByRcptTo(nameList);
			}, threadPool);
			try {
				CompletableFuture.allOf(f1, f2, f3).get();
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
				return Result.error().message("删除用户失败");
			}
		}
		return Result.ok();
	}

	/**
	 * 获取当前登录用户的所有黑名单
	 */
	@GetMapping("/blackList/all")
	public Result getBlackList(String username) {
		List<BlackList> blackList = blackListMapper.listBlackList(username);
		return Result.ok().data("blackList", blackList);
	}

	/**
	 * 设置黑名单
	 */
	@PostMapping("/blackList/set")
	public Result setBlackList(@RequestBody @Valid BlackList blackList) {
		int i = blackListMapper.selectExist(blackList);
		if (i > 0) {
			return Result.error().message("添加黑名单失败：该名单已存在");
		}
		blackListMapper.insert(blackList);
		return Result.ok().message("添加黑名单成功");
	}

	/**
	 * 删除黑名单
	 */
	@DeleteMapping("/blackList/remove")
	public Result removeBlack(@RequestBody @Validated(DeleteGroup.class) List<Long> idList) {
		String username = jwtUtil.getUserToken().getUsername();
		blackListMapper.removeBatch(idList, username);
		return Result.ok();
	}

	/**
	 * 获取当前用户的所有星标邮件
	 */
	@GetMapping("/star/all")
	public Result getAllStarEmails() {
		String username = jwtUtil.getUserToken().getUsername();
		List<StarEmail> list = starEmailMapper.list(username);
		return Result.ok().data("starEmails", list);
	}

	/**
	 * 设置星标邮件
	 */
	@GetMapping("/star")
	public Result setStarEmail(@RequestParam String emailId) {
		String username = jwtUtil.getUserToken().getUsername();
		Email email = emailMapper.selectById(emailId);
		if (email == null) {
			return Result.error().message("当前邮件不存在");
		}
		StarEmail starEmail = new StarEmail(emailId, username);
		starEmailMapper.insert(starEmail);
		return Result.ok().message("设置星标邮件成功");
	}

	/**
	 * 取消星标邮件
	 */
	@GetMapping("/unstar")
	public Result resetStarEmail(@RequestParam String emailId) {
		starEmailMapper.deleteById(emailId);
		return Result.ok().message("取消星标邮件成功");
	}

	/**
	 * 获取当前头像
	 */
	@GetMapping("/avatar")
	public Result getUserAvatar() {
		String avatar = jwtUtil.getUserToken().getAvatar();
		if (!StringUtils.hasText(avatar)) {
			avatar = null;
		}
		return Result.ok().data("avatar", avatar);
	}

	/**
	 * 上传用户头像
	 */
	@PostMapping("/avatar/upload")
	public Result uploadAvatar(@RequestBody MultipartFile file) {
		String username = jwtUtil.getUserToken().getUsername();
		InputStream is = null;
		try {
			is = file.getInputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
		String fileName = file.getOriginalFilename();
		String url = ossUtil.saveObject(is, username.substring(0, username.indexOf('@')), fileName);
		CompletableFuture<Void> f1 = CompletableFuture.runAsync(() -> {
			userService.saveAvatar(username, url);
		}, threadPool);
		CompletableFuture<Void> f2 = CompletableFuture.runAsync(() -> {
			avatarMapper.save(new Avatar(username, url, new Date()));
		}, threadPool);
		// 保证数据一致性
		CompletableFuture<Void> f3 = CompletableFuture.runAsync(() -> {
			publisherMapper.updateAvatar(username, url);
		}, threadPool);
		try {
			CompletableFuture.allOf(f1, f2, f3).get();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
			return Result.error().message("上传失败");
		}
		return Result.ok().data("url", url);
	}

	/**
	 * 获取用户使用过的所有头像
	 */
	@GetMapping("/avatar/all")
	public Result getUserAvatars() {
		String username = jwtUtil.getUserToken().getUsername();
		List<Avatar> avatars = avatarMapper.findAllByUsername(username);
		return Result.ok().data("avatars", avatars);
	}

	/**
	 * 删除使用过的头像
	 */
	@DeleteMapping("/avatar/remove")
	public Result removeUsedAvatar(@RequestBody List<Avatar> avatarList) {
		String username = jwtUtil.getUserToken().getUsername();
		avatarMapper.remove(username, avatarList);
		return Result.ok();
	}

	/**
	 * 管理员接口： 用户注册统计：2周内注册的用户统计
	 */
	@GetMapping("/statistics")
	public Result registerCount() {
		if (!jwtUtil.isManager()) {
			log.error("【{}】用户尝试获取用户统计数据，失败原因：无权限", jwtUtil.getUserToken().getUsername());
			return Result.error().message("无权限");
		}
		Map<Long, Map<String, Long>> resultMap = userService.statistics();
		Entry[] list = new Entry[14];
		Long sum = 0L;
		for (int i = 13; i >= 0; i--) {
			if (resultMap.get((long) i) != null) {
				Map<String, Long> result = resultMap.get((long) i);
				sum += result.get("count");
			}
			list[i] = new Entry(i, sum);
		}
		return Result.ok().data("list", list);
	}
}
