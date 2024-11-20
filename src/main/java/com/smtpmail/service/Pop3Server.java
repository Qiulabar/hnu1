package com.smtpmail.service;

import com.smtpmail.pop3.server.Pop3ServerChannelInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Pop3Server {
	private final static AtomicBoolean isPop3pRunning = new AtomicBoolean(false);
	private static Thread pop3Thread;
	private static NioEventLoopGroup bossGroup;
	private static NioEventLoopGroup workGroup;
	private static Integer POP3_PORT = 110;

	private static void start() {
		isPop3pRunning.set(true);
		log.info("Started Pop3Server on Port {}", POP3_PORT);
		bossGroup = new NioEventLoopGroup();
		workGroup = new NioEventLoopGroup();
		pop3Thread = new Thread(Pop3Server::run);
		pop3Thread.start();
	}

	public static void start(int port) {
		if (isPop3pRunning.get()) {
			log.error("Pop3Server Start Error: Already running on Port {}", POP3_PORT);
			return;
		}
		Pop3Server.POP3_PORT = port;
		Pop3Server.start();
	}

	public static void stop() {
		if (!isPop3pRunning.get()) {
			log.error("Pop3Server Stop Error: Pop3Server is Not Running");
			return;
		}
		isPop3pRunning.set(false);
		log.info("Pop3Server is Stopped");
		shutDown();
		pop3Thread.interrupt();
	}

	public static boolean isRunning() {
		return isPop3pRunning.get();
	}

	public static Integer getRunningPort() {
		return POP3_PORT;
	}

	private static void run() {
		try {
			ServerBootstrap bootstrap = new ServerBootstrap();
			ChannelFuture future = bootstrap.group(bossGroup, workGroup)
					.channel(NioServerSocketChannel.class)
					.childHandler(new Pop3ServerChannelInitializer())
					.bind(POP3_PORT).sync();

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
