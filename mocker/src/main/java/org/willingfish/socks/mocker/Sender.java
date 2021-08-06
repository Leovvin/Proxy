package org.willingfish.socks.mocker;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.willingfish.socks.mocker.handler.PrintHandler;

import java.nio.charset.StandardCharsets;

public class Sender {

    Channel channel;

    public Sender() throws InterruptedException {


        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(new NioEventLoopGroup(1))
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY,true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline()
                                .addLast(new PrintHandler())
                                ;
                    }
                });
        channel = bootstrap.connect("localhost",8388).sync().channel();
    }

    public void send(String s){
        byte[] data = s.getBytes(StandardCharsets.UTF_8);
        ByteBuf bf = channel.alloc().buffer(data.length);
        bf.writeBytes(data);
        channel.writeAndFlush(bf);
    }
}
