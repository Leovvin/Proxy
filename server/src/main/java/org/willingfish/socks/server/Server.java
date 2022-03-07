package org.willingfish.socks.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.socksx.v4.Socks4ServerDecoder;
import io.netty.handler.codec.socksx.v4.Socks4ServerEncoder;
import io.netty.handler.codec.socksx.v5.Socks5CommandRequestDecoder;
import io.netty.handler.codec.socksx.v5.Socks5InitialRequestDecoder;
import io.netty.handler.codec.socksx.v5.Socks5ServerEncoder;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.willingfish.socks.common.IServer;
import org.willingfish.socks.common.handler.ProxyIdleHandler;
import org.willingfish.socks.common.ssl.ISslEngineFactory;
import org.willingfish.socks.server.hanlder.Socks4CommandRequestHandler;
import org.willingfish.socks.server.hanlder.Socks5CommandRequestHandler;
import org.willingfish.socks.server.hanlder.Socks5InitialRequestHandler;

import javax.net.ssl.SSLEngine;


@Slf4j
public class Server implements IServer, ApplicationContextAware {
    @Setter
    Integer port;
    @Setter
    ISslEngineFactory sslEngineFactory;

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
                        SSLEngine engine = sslEngineFactory.createSslEngine();
                        engine.setUseClientMode(false);//设置服务端模式
                        engine.setNeedClientAuth(true);//需要客户端验证

                        ch.pipeline()
                                .addLast(new IdleStateHandler(3, 30, 0))
                                .addLast(new ProxyIdleHandler())
                                .addLast(new SslHandler(engine))
                                .addLast(Socks5ServerEncoder.DEFAULT)
                                .addLast(new Socks5InitialRequestDecoder())
                                .addLast(new Socks5InitialRequestHandler())
                                .addLast(new Socks5CommandRequestDecoder())
                                .addLast(new Socks5CommandRequestHandler())
                        ;
                    }
                })
                .option(ChannelOption.SO_BACKLOG, 128) // determining the number of connections queued
                .childOption(ChannelOption.SO_KEEPALIVE, Boolean.TRUE);

        try {
            ChannelFuture future = b.bind(port).sync();
            log.info("listen on port:{}", port);
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("server meet unknown exception.", e);
        } finally {
            boss.shutdownGracefully();
        }

    }

    ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
