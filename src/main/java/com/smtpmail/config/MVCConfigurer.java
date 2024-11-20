package com.smtpmail.config;

import com.smtpmail.filters.AuthInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
@Configuration
public class MVCConfigurer implements WebMvcConfigurer {

	/**
	 * 添加拦截器
	 */
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(new AuthInterceptor())
				.addPathPatterns("/smtpmail/user/**")
				.excludePathPatterns("/smtpmail/user/login", "/smtpmail/user/manager/login")
				.excludePathPatterns("/smtpmail/log/**")
				.excludePathPatterns("/smtpmail/user/register", "/smtpmail/user/phoneAuth");
				// 下方是测试接口
				// .excludePathPatterns("/smtpmail/user/avatar/**");
	}

	/**
	 * 跨域处理
	 */
	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**")
				.allowedHeaders("*")
				.allowedMethods("*")
				.allowedOrigins("*")
				.allowCredentials(true)
				.exposedHeaders("SMTP_TOKEN")
				.maxAge(3600L);
	}
}
