package com.smtpmail.service;

import com.smtpmail.smtp.server.SmtpServerChannelInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SmtpServer {
	private final static AtomicBoolean isSmtpRunning = new AtomicBoolean(false);
	private static Thread smtpThread;
	private static NioEventLoopGroup bossGroup;
	private static NioEventLoopGroup workGroup;
	private static Integer SMTP_PORT = 25;

	private static void start() {
		isSmtpRunning.set(true);
		log.info("Started SmtpServer on Port {}", SMTP_PORT);
		bossGroup = new NioEventLoopGroup();
		workGroup = new NioEventLoopGroup();
		smtpThread = new Thread(SmtpServer::run);
		smtpThread.start();
	}

	public static void start(int port) {
		if (isSmtpRunning.get()) {
			log.error("SmtpServer Start Error: Already running on Port {}", SMTP_PORT);
			return;
		}
		SmtpServer.SMTP_PORT = port;
		SmtpServer.start();
	}

	public static void stop() {
		if (!isSmtpRunning.get()) {
			log.error("SmtpServer Stop Error: SmtpServer is Not Running");
			return;
		}
		isSmtpRunning.set(false);
		log.info("SmtpServer is Stopped");
		shutDown();
		smtpThread.stop();
	}

	public static boolean isRunning() {
		return isSmtpRunning.get();
	}

	public static Integer getRunningPort() {
		return SMTP_PORT;
	}

	private static void run() {
		try {
			ServerBootstrap bootstrap = new ServerBootstrap();
			ChannelFuture future = bootstrap.group(bossGroup, workGroup)
					.channel(NioServerSocketChannel.class)
					.childHandler(new SmtpServerChannelInitializer())
					.bind(SMTP_PORT).sync();
			// 阻塞监听服务器的关闭事件
			future.channel().closeFuture().sync();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			shutDown();
		}
	}

	private static void shutDown() {
		bossGroup.shutdownGracefully();
		workGroup.shutdownGracefully();
	}
}
