package com.smtpmail.exception;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
@Configuration
public class ServerContextHolder {

	@Bean
	public ExecutorService threadPool() {
		return Executors.newFixedThreadPool(4);
	}

	public static ApplicationContext context;

	public static Environment environment;

	public String getProperty(String key, String defaultValue) {
		if (environment == null) {
			environment = context.getEnvironment();
		}
		return environment.getProperty(key, defaultValue);
	}

	public static String getProperty(String key) {
		if (environment == null) {
			environment = context.getEnvironment();
		}
		return environment.getProperty(key);
	}
}
