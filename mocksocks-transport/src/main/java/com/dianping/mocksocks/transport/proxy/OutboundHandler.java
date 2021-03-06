package com.dianping.mocksocks.transport.proxy;

import com.dianping.mocksocks.transport.Connection;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.*;

/**
 * @author yihua.huang@dianping.com
 */
public class OutboundHandler extends SimpleChannelUpstreamHandler {

	private final Channel inboundChannel;

	private String name;

	private Connection connection;

	OutboundHandler(Channel inboundChannel, Connection connection) {
		this.inboundChannel = inboundChannel;
		this.connection = connection;
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx, final MessageEvent e) throws Exception {
		final ChannelBuffer msg = (ChannelBuffer) e.getMessage();
		inboundChannel.write(msg);
		super.messageReceived(ctx, e);
	}

	@Override
	public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
		closeOnFlush(inboundChannel);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
		closeOnFlush(e.getChannel());
		inboundChannel.close();
	}

	/**
	 * Closes the specified channel after all queued write requests are flushed.
	 */
	static void closeOnFlush(Channel ch) {
		if (ch.isConnected()) {
			ch.write(ChannelBuffers.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
		}
	}

	public String getName() {
		return name;
	}
}
