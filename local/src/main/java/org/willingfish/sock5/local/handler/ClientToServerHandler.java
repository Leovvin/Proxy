package org.willingfish.sock5.local.handler;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.willingfish.sock5.common.handler.CipherToPlainDecoder;
import org.willingfish.sock5.common.handler.PlainToCipherEncoder;

@Slf4j
public class ClientToServerHandler extends ChannelInboundHandlerAdapter {
    @Setter
    String server;
    @Setter
    Integer port;
    @Setter
    CipherToPlainDecoder cipherToPlainDecoder;
    @Setter
    PlainToCipherEncoder plainToCipherEncoder;

    static NioEventLoopGroup group = new NioEventLoopGroup();

    Channel clientChannel;
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (clientChannel == null){
            clientChannel = createClientChannel(ctx);
        }
        clientChannel.writeAndFlush(msg);
    }

    Channel createClientChannel(ChannelHandlerContext ctx) throws InterruptedException {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline()
                                .addLast(new LengthFieldBasedFrameDecoder(65536+4,0,4))
                                .addLast(cipherToPlainDecoder)
                                .addLast(plainToCipherEncoder)
                                .addLast(new Server2ClientHandler(ctx));
                    }
                });
        return bootstrap.connect(server,port).sync().channel();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        clientChannel.close();
    }

    private class Server2ClientHandler extends ChannelInboundHandlerAdapter {

        private ChannelHandlerContext clientChannelContext;

        public Server2ClientHandler(ChannelHandlerContext clientChannelContext) {
            this.clientChannelContext = clientChannelContext;
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx2, Object destMsg) throws Exception {
            log.info("send data from target server to client");
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
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)  {
            log.error("dest to local channel meet exception",cause);
            ctx.channel().close();
        }
    }

}
