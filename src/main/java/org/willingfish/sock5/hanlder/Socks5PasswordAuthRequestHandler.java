package org.willingfish.sock5.hanlder;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.socksx.v5.DefaultSocks5PasswordAuthRequest;
import io.netty.handler.codec.socksx.v5.DefaultSocks5PasswordAuthResponse;
import io.netty.handler.codec.socksx.v5.Socks5PasswordAuthResponse;
import io.netty.handler.codec.socksx.v5.Socks5PasswordAuthStatus;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Socks5PasswordAuthRequestHandler extends SimpleChannelInboundHandler<DefaultSocks5PasswordAuthRequest> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DefaultSocks5PasswordAuthRequest msg) throws Exception {
        log.debug("用户名密码 : " + msg.username() + "," + msg.password());
//        if(passwordAuth.auth(msg.username(), msg.password())) {
//            ProxyChannelTrafficShapingHandler.username(ctx, msg.username());
//            Socks5PasswordAuthResponse passwordAuthResponse = new DefaultSocks5PasswordAuthResponse(Socks5PasswordAuthStatus.SUCCESS);
//            ctx.writeAndFlush(passwordAuthResponse);
//        } else {
//            ProxyChannelTrafficShapingHandler.username(ctx, "unauthorized");
//            Socks5PasswordAuthResponse passwordAuthResponse = new DefaultSocks5PasswordAuthResponse(Socks5PasswordAuthStatus.FAILURE);
//            //发送鉴权失败消息，完成后关闭channel
//            ctx.writeAndFlush(passwordAuthResponse).addListener(ChannelFutureListener.CLOSE);
//        }
        Socks5PasswordAuthResponse passwordAuthResponse = new DefaultSocks5PasswordAuthResponse(Socks5PasswordAuthStatus.SUCCESS);
        ctx.writeAndFlush(passwordAuthResponse);
    }
}
