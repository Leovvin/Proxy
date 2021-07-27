package org.willingfish.sock5.common.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import lombok.Setter;
import org.willingfish.sock5.common.ciper.AESCoder;

import java.util.List;

@Sharable
public class CipherToPlainDecoder extends MessageToMessageDecoder<ByteBuf> {
    @Setter
    AESCoder aesCoder;
    @Setter
    Integer maxPayloadLength;
    @Setter
    Integer headerLength;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> out) throws Exception {
        int payloadLength = byteBuf.readInt();
        ByteBuf frameBuf = byteBuf.readSlice(payloadLength);
        byte[] cipher = new byte[payloadLength];
        frameBuf.readBytes(cipher);

        byte[] plain = aesCoder.decrypt(cipher);
        ByteBuf plainBlock = Unpooled.buffer(plain.length);
        plainBlock.writeBytes(plain);
        out.add(plainBlock);
    }
}
