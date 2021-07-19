package org.willingfish.sock5.serv;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.socksx.v5.Socks5CommandRequestDecoder;
import io.netty.handler.codec.socksx.v5.Socks5InitialRequestDecoder;
import io.netty.handler.codec.socksx.v5.Socks5ServerEncoder;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.willingfish.sock5.common.IServer;
import org.willingfish.sock5.common.handler.CipherToPlainDecoder;
import org.willingfish.sock5.common.handler.PlainToCipherEncoder;
import org.willingfish.sock5.common.handler.ProxyIdleHandler;
import org.willingfish.sock5.serv.hanlder.Socks5CommandRequestHandler;
import org.willingfish.sock5.serv.hanlder.Socks5InitialRequestHandler;


@Slf4j
public class Server implements IServer, ApplicationContextAware {
    @Setter
    Integer port;
    @Setter
    CipherToPlainDecoder cipherToPlainDecoder;
    @Setter
    PlainToCipherEncoder plainToCipherEncoder;

    public void start() {

        ServerBootstrap b = new ServerBootstrap();
        NioEventLoopGroup group = new NioEventLoopGroup();
        b.group(group)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch)
                            throws Exception {
                        System.out.println("initChannel ch:" + ch);
                        ch.pipeline()
                                .addLast(new IdleStateHandler(3, 30, 0))
                                .addLast(new ProxyIdleHandler())
                                .addLast(cipherToPlainDecoder)
                                .addLast(plainToCipherEncoder)
                                .addLast(new LoggingHandler())
                                .addLast(Socks5ServerEncoder.DEFAULT)
                                .addLast(new Socks5InitialRequestDecoder())
                                .addLast(new Socks5InitialRequestHandler())
                                .addLast(new Socks5CommandRequestDecoder())
                                .addLast(new Socks5CommandRequestHandler(group));
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
            group.shutdownGracefully();
        }

    }

    ApplicationContext applicationContext;
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
