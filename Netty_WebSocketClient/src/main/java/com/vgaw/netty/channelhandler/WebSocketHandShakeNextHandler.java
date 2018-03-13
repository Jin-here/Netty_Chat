package com.vgaw.netty.channelhandler;

import com.vgaw.netty.pojo.BridgeProto;
import com.vgaw.netty.pojo.TalkProto;
import com.vgaw.netty.pojo.UserProto;
import com.vgaw.netty.util.Util;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketClientProtocolHandler;
import io.netty.util.CharsetUtil;

import java.nio.charset.Charset;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Administrator on 2015/9/1.
 */
public class WebSocketHandShakeNextHandler extends SimpleChannelInboundHandler<Object> {
    /*
    * To know
    * once a handshake was done you can intercept the
    * ChannelHandler.userEventTriggered(ChannelHandlerContext, Object) and
    * check if the event was of type
    * WebSocketClientProtocolHandler.ClientHandshakeStateEvent.HANDSHAKE_ISSUED or
    * WebSocketClientProtocolHandler.ClientHandshakeStateEvent.HANDSHAKE_COMPLETE.
    **/
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt == WebSocketClientProtocolHandler.ClientHandshakeStateEvent.HANDSHAKE_COMPLETE){
            //when shake hand succeed,we should tell the server that we are online,
            //that is to say,we should send a online msg.
            String description = Util.getName(2);
            BridgeProto.Bridge bridge = BridgeProto.Bridge.newBuilder()
                    .setStatus(BridgeProto.Bridge.Status.ONLINE)
                    .setDescription(description)
                    .build();
            byte[] byteArray = bridge.toByteArray();
            ByteBuf directBuf = ctx.alloc().directBuffer(byteArray.length, byteArray.length).writeBytes(byteArray);
            ctx.writeAndFlush(new TextWebSocketFrame(directBuf));

            System.out.println("Welcome to the chat room : " + description);
        }else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, Object msg) throws Exception {
        // Text and Binary data frames
        // the close frame
        if (msg instanceof TextWebSocketFrame){
            ByteBuf directBuf = ((TextWebSocketFrame) msg).content();
            int length = directBuf.readableBytes();
            byte[] raw = new byte[length];
            directBuf.getBytes(directBuf.readerIndex(), raw);

            BridgeProto.Bridge bridge = BridgeProto.Bridge.parseFrom(raw);
            switch (bridge.getStatus()){
                case SUCCESS:
                    break;
                case FAILED:
                    break;
                default:
                    break;
            }
        }
        if (msg instanceof BinaryWebSocketFrame){
            //when you pass a msg to the next handler,you should use as msg.retain(),
            //cause the current when call ReferenceCountUtil.release(msg) when it is over
            //you can refer to MessageToMessageDecoder#channelRead() method
            ctx.fireChannelRead(((BinaryWebSocketFrame) msg).retain());
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
