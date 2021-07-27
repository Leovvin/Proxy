package org.willingfish.sock5.local;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.willingfish.sock5.common.IServer;
import org.willingfish.sock5.local.handler.ClientToServerHandler;


@Slf4j
public class Server implements IServer, ApplicationContextAware {
    @Setter
    Integer localPort;

    ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public void start() {

        ServerBootstrap b = new ServerBootstrap();
        NioEventLoopGroup boss = new NioEventLoopGroup(1);
        NioEventLoopGroup worker = new NioEventLoopGroup();
        b.group(boss, worker)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch)
                            throws Exception {
                        ch.pipeline()
                                .addLast(applicationContext.getBean(ClientToServerHandler.class));
                    }
                })
                .option(ChannelOption.SO_BACKLOG, 128) // determining the number of connections queued
                .childOption(ChannelOption.SO_KEEPALIVE, Boolean.TRUE);

        try {
            ChannelFuture future = b.bind(localPort).sync();
            log.info("listen on port:{}", localPort);
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("server meet unknown exception.", e);
        } finally {
            boss.shutdownGracefully();
        }

    }
}
