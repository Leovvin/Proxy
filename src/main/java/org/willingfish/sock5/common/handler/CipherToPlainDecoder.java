package org.willingfish.sock5.common.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import lombok.Setter;
import org.willingfish.sock5.ciper.AESCoder;

import java.util.List;

@Sharable
public class CipherToPlainDecoder extends MessageToMessageDecoder<ByteBuf> {
    @Setter
    AESCoder aesCoder;
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> out) throws Exception {
        ByteBuf block = byteBuf.readSlice(255);
        byte[] plain = aesCoder.decrypt(block.array());
        ByteBuf plainBlock = Unpooled.buffer(plain.length);
        plainBlock.writeBytes(plain);
        out.add(plainBlock);
    }
}
