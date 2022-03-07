package org.willingfish.socks.server.hanlder;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.socksx.v5.*;
import lombok.extern.slf4j.Slf4j;

import java.net.Inet4Address;
import java.net.InetAddress;

@Slf4j
public class Socks5CommandRequestHandler extends SimpleChannelInboundHandler<DefaultSocks5CommandRequest> {
    static EventLoopGroup group = new NioEventLoopGroup();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DefaultSocks5CommandRequest msg) throws Exception {
        InetAddress address = Inet4Address.getByName(msg.dstAddr());
        log.info("目标服务器  : " + msg.type() + "," + msg.dstAddr() + "," + address.getHostAddress() + "," + msg.dstPort());
        if(msg.type().equals(Socks5CommandType.CONNECT)) {
            log.trace("准备连接目标服务器");

            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            //ch.pipeline().addLast(new LoggingHandler());//in out
                            //将目标服务器信息转发给客户端
                            ch.pipeline().addLast(new Dest2LocalHandler(ctx));
                        }
                    });
            log.trace("连接目标服务器");
            bootstrap.connect(address.getHostAddress(), msg.dstPort())
                    .addListener((ChannelFutureListener) future -> {
                        if(future.isSuccess()) {
                            log.trace("成功连接目标服务器");
                            ctx.pipeline().addLast(new Local2DestHandler(future));
                            DefaultSocks5CommandResponse commandResponse =
                                    new DefaultSocks5CommandResponse(Socks5CommandStatus.SUCCESS, Socks5AddressType.IPv4);
                            ctx.writeAndFlush(commandResponse);
                        } else {
                            DefaultSocks5CommandResponse commandResponse =
                                    new DefaultSocks5CommandResponse(Socks5CommandStatus.FAILURE, Socks5AddressType.IPv4);
                            ctx.writeAndFlush(commandResponse);
                        }
                    });
        }else {
            ctx.fireChannelRead(msg);
        }
    }

    /**
     * 将目标服务器信息转发给客户端
     *
     * @author huchengyi
     *
     */
    private static class Dest2LocalHandler extends ChannelInboundHandlerAdapter {

        private ChannelHandlerContext clientChannelContext;

        public Dest2LocalHandler(ChannelHandlerContext clientChannelContext) {
            this.clientChannelContext = clientChannelContext;
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx2, Object destMsg) throws Exception {
            log.debug("send data from target server to client");
            if (destMsg==null){
                return;
            }
            if (destMsg instanceof ByteBuf){
                ByteBuf byteBuf = (ByteBuf) destMsg;
                if (byteBuf.readableBytes()<=0){
                    return;
                }
            }
            clientChannelContext.writeAndFlush(destMsg);
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx2) throws Exception {
            log.debug("break target server connect");
            clientChannelContext.channel().close();
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            log.error("dest to local channel meet exception",cause);
            ctx.channel().close();
        }
    }

    /**
     * 将客户端的消息转发给目标服务器端
     *
     * @author huchengyi
     *
     */
    private static class Local2DestHandler extends ChannelInboundHandlerAdapter {

        private ChannelFuture destChannelFuture;

        public Local2DestHandler(ChannelFuture destChannelFuture) {
            this.destChannelFuture = destChannelFuture;
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            log.debug("send data from client to target server");
            if (msg==null){
                return;
            }
            if (msg instanceof ByteBuf){
                ByteBuf byteBuf = (ByteBuf) msg;
                if (byteBuf.readableBytes()<=0){
                    return;
                }
            }
            destChannelFuture.channel().writeAndFlush(msg);
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            log.debug("break client connect");
            destChannelFuture.channel().close();
        }


        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            log.error("local to dest channel failed",cause);
            ctx.channel().close();
        }
    }
}
