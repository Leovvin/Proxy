package org.willingfish.sock5.common.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import lombok.Setter;
import org.willingfish.sock5.common.ciper.AESCoder;

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
        byte[] plain;
        if (byteBuf.readableBytes() > maxPayloadLength) {
            plain = new byte[maxFrameLength];
            byteBuf.readSlice(maxFrameLength).readBytes(plain);
        }else {
            plain = new byte[byteBuf.readableBytes()];
            byteBuf.readBytes(plain);
        }
        byte[] cipher = aesCoder.encrypt(plain);
        ByteBuf buf = PooledByteBufAllocator.DEFAULT.buffer(cipher.length+headerLength);
        buf.writeInt(cipher.length);
        buf.writeBytes(cipher);
        out.add(buf);
    }
}
