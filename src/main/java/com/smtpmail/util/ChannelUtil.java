package com.smtpmail.util;

import io.netty.channel.Channel;
import java.net.InetSocketAddress;

public class ChannelUtil {

	public static String getRemoteIpAsString(Channel channel) {
		InetSocketAddress socketAddress = (InetSocketAddress) channel.remoteAddress();
		return socketAddress.getAddress().getHostAddress();
	}

	public static String getRemotePortAsString(Channel channel) {
		return getRemotePortAsInt(channel) + "";
	}

	public static int getRemotePortAsInt(Channel channel) {
		InetSocketAddress socketAddress = (InetSocketAddress) channel.remoteAddress();
		return socketAddress.getPort();
	}
}
