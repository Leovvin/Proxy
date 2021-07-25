package org.willingfish.sock5.common.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EchoHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof ByteBuf){
            ByteBuf in = (ByteBuf) msg;
            System.out.println("Server received: " + in.toString(CharsetUtil.UTF_8));
            ctx.writeAndFlush(in);	//
        }

    }
}
