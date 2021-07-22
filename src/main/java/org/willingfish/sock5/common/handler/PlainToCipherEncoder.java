package org.willingfish.sock5.common.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import lombok.Setter;
import org.willingfish.sock5.ciper.AESCoder;

import java.util.List;

@Sharable
public class PlainToCipherEncoder extends MessageToMessageEncoder<ByteBuf> {
    @Setter
    AESCoder aesCoder;
    @Setter
    Integer maxPayloadLength;
    @Setter
    Integer headerLength;

    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> out) throws Exception {
        int maxFrameLength = maxPayloadLength + headerLength;
        if (byteBuf.readableBytes() > maxPayloadLength) {

        }
    }
}
