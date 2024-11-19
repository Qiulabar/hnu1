package com.smtpmail.start;

import com.smtpmail.exception.ServerContextHolder;
import com.smtpmail.service.Pop3Server;
import com.smtpmail.service.SmtpServer;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableCaching
@SpringBootApplication
@EnableTransactionManagement
@MapperScan("com.smtpmail.mapper")
@Slf4j
public class SmtpMailApplication {

	public static void main(String[] args) {
		// Start WebServer on 8080 Port
		ServerContextHolder.context = SpringApplication.run(SmtpMailApplication.class, args);
		log.info("Started WebServer on Port {}", ServerContextHolder.getProperty("server.port"));

		// Start SmtpServer on 25 Port
		SmtpServer.start(25);
		// Start Pop3Server on 110 Port
		Pop3Server.start(110);
		// 日志文件保存路径
		log.info("日志文件保存路径：{}", ServerContextHolder.getProperty("logging.file.path"));
	}
}
