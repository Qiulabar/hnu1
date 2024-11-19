package com.smtpmail.util;

import com.alibaba.fastjson.JSON;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.smtpmail.entity.UserToken;
import com.smtpmail.exception.TokenException;
import com.smtpmail.exception.UnLoginException;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@ConfigurationProperties(prefix = "jwt")
public class JWTUtil {

	// 存进客户端的token的key名
	public static final String TOKEN_KEY = "SMTP_TOKEN";
	// 定义token返回头部
	public static String header;
	// token前缀
	public static String tokenPrefix;
	// 签名密钥
	public static String secret;
	// 有效期（毫秒）
	public static long expireTime;

	@Autowired
	HttpServletRequest request;


	/**
	 * 创建TOKEN
	 */
	public static String createToken(String sub) {
		return tokenPrefix + JWT.create()
				.withSubject(sub)
				.withExpiresAt(new Date(System.currentTimeMillis() + expireTime))
				.sign(Algorithm.HMAC512(secret));
	}

	/**
	 * 验证token
	 */
	public static String validateToken(String token) throws TokenException {
		try {
			return JWT.require(Algorithm.HMAC512(secret))
					.build()
					.verify(token.replace(tokenPrefix, ""))
					.getSubject();
		} catch (TokenExpiredException e) {
			throw new TokenException("token已经过期");
		} catch (Exception e) {
			throw new TokenException("token验证失败");
		}
	}

	/**
	 * 检查token是否需要更新
	 */
	public static boolean isNeedUpdate(String token) throws TokenException {
		//获取token过期时间
		Date expiresAt = null;
		try {
			expiresAt = JWT.require(Algorithm.HMAC512(secret))
					.build()
					.verify(token.replace(tokenPrefix, ""))
					.getExpiresAt();
			long timeDiff = expiresAt.getTime() - System.currentTimeMillis();
			if (timeDiff > JWTUtil.expireTime) {
				throw new TokenException("不合法的token，请重新登录");
			}
		} catch (TokenExpiredException e) {
			return true;
		} catch (Exception e) {
			throw new TokenException("token已经过期");
		}
		// 如果剩余过期时间少于过期时常的一半时 需要更新
		return (expiresAt.getTime() - System.currentTimeMillis()) < (expireTime >> 1);
	}

	public void setHeader(String header) {
		JWTUtil.header = header;
	}

	public void setTokenPrefix(String tokenPrefix) {
		JWTUtil.tokenPrefix = tokenPrefix;
	}

	public void setSecret(String secret) {
		JWTUtil.secret = secret;
	}

	public void setExpireTime(long expireTime) {
		JWTUtil.expireTime = expireTime;
	}

	public UserToken getUserToken() {
		String token = request.getHeader(JWTUtil.TOKEN_KEY);
		return this.getUserToken(token);
	}

	public UserToken getUserToken(String token) {
		if (!StringUtils.hasText(token)) {
			throw new UnLoginException();
		}
		String userJSON = null;
		try {
			userJSON = JWTUtil.validateToken(token);
		} catch (TokenException e) {
			return null;
		}
		return JSON.parseObject(userJSON, UserToken.class);
	}

	public boolean isManager() {
		UserToken userToken = getUserToken();
		return userToken != null && userToken.getLevel() == 0;
	}
}