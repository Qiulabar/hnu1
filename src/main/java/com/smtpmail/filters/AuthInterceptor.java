package com.smtpmail.filters;

import com.alibaba.fastjson.JSON;
import com.smtpmail.common.Result;
import com.smtpmail.entity.UserToken;
import com.smtpmail.exception.TokenException;
import com.smtpmail.exception.UnLoginException;
import com.smtpmail.util.JWTUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 请求拦截器：通过检查请求头中的Token字段，查看用户是否登录
 */
@Order(1)
@Slf4j
public class AuthInterceptor implements HandlerInterceptor {

	/**
	 * 在除login的所有请求之前，查看token验证身份
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
		if ("OPTIONS".equals(request.getMethod())) {
			log.warn("OPTIONS请求发送");
			return true;
		}
		try {
			String token = request.getHeader(JWTUtil.TOKEN_KEY);
			response.setCharacterEncoding("utf-8");
			response.setContentType("text/html;charset=utf-8");
			if (!StringUtils.hasText(token)) {
				response.getWriter().write(
						JSON.toJSONString(Result.error().code(400).message("请先登录"))
				);
				return false;
			}
			String userJSON = JWTUtil.validateToken(token);
			UserToken user = JSON.parseObject(userJSON, UserToken.class);
			if (user.getLevel() == -1) { // 如果用户已经被禁用，则无法登录
				response.getWriter().write(
						JSON.toJSONString(Result.error().message("用户已被禁用"))
				);
				return false;
			}
			if (JWTUtil.isNeedUpdate(token)) {
				String newToken = JWTUtil.createToken(token);
				response.setHeader(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, JWTUtil.TOKEN_KEY);
				response.setHeader(JWTUtil.TOKEN_KEY, newToken);
			}
		} catch (TokenException | IOException e) {
			throw new UnLoginException("登录信息过期，请重新登录");
		}
		return true;
	}
}
