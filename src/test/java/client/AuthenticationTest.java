package client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.junit.jupiter.api.Test;

public class AuthenticationTest {
	private static final String IP = "127.0.0.1";
	private static final int PORT = 25;

	@Test
	public void test01() {
		NioEventLoopGroup workGroup = new NioEventLoopGroup();

		Bootstrap bootstrap = new Bootstrap();

		try {
			ChannelFuture future = bootstrap.group(workGroup)
					.channel(NioSocketChannel.class)
					.handler(new ChannelInitializer<SocketChannel>() {
						@Override
						protected void initChannel(SocketChannel ch) {
							ch.pipeline()
									.addLast(new DelimiterBasedFrameDecoder(1024, Delimiters.lineDelimiter()))
									.addLast(new StringDecoder())
									.addLast(new StringEncoder())
									.addLast(new SmtpAuthMessageHandler());
						}
					})
					.bind(IP, PORT).sync();

			future.channel().closeFuture().sync();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			workGroup.shutdownGracefully();
		}
	}
}


class SmtpAuthMessageHandler extends SimpleChannelInboundHandler<String> {

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, String msg) {
		System.out.println(msg);
	}
}