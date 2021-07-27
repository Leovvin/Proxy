package org.willingfish.sock5.mocker;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.util.internal.StringUtil;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.willingfish.sock5.common.handler.CipherToPlainDecoder;
import org.willingfish.sock5.common.handler.PlainToCipherEncoder;


import java.nio.charset.StandardCharsets;

@Slf4j
public class Mocker {
    @Setter
    String server;
    @Setter
    Integer port;
    @Setter
    CipherToPlainDecoder cipherToPlainDecoder;
    @Setter
    PlainToCipherEncoder plainToCipherEncoder;

    Channel channel;

    void init() throws InterruptedException {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(new NioEventLoopGroup())
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ch.pipeline()
                                .addLast(new LengthFieldBasedFrameDecoder(65536,0,4))
                                .addLast(cipherToPlainDecoder)
                                .addLast(plainToCipherEncoder)
                                .addLast(new PrintHandler())
                        ;
                    }
                });
        channel = bootstrap.connect(server, port).sync().channel();
    }

    public void send(String s) throws InterruptedException {
        if (StringUtil.isNullOrEmpty(s)) {
            return;
        }
        byte[] data = s.getBytes(StandardCharsets.UTF_8);
        ByteBuf bf = channel.alloc().buffer(data.length);
        bf.writeBytes(data);
        channel.writeAndFlush(bf).sync();
    }
}
