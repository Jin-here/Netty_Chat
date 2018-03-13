package com.vgaw.netty.channelhandler;

import com.vgaw.netty.pojo.BridgeProto;
import com.vgaw.netty.pojo.TalkProto;
import com.vgaw.netty.pojo.UserProto;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Administrator on 2015/8/31.
 */
public class WebSocketHandShakeNextHandler extends SimpleChannelInboundHandler<Object> {
    private final ChannelGroup group;

    private static ConcurrentHashMap<String, ChannelId> channelIdMap = new ConcurrentHashMap<String, ChannelId>();

    public WebSocketHandShakeNextHandler(ChannelGroup group){
        this.group = group;
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt == WebSocketServerProtocolHandler.ServerHandshakeStateEvent.HANDSHAKE_COMPLETE){
            //ctx.pipeline().remove(HttpRequestHandler.class);
            //System.out.println("Client " + ctx.channel() + " online");
            group.add(ctx.channel());

        }else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, Object msg) throws Exception {
        Channel me = ctx.channel();
        if (msg instanceof TextWebSocketFrame){
            ByteBuf raw = ((TextWebSocketFrame) msg).content();
            int length = raw.readableBytes();
            byte[] rawArray = new byte[length];
            raw.getBytes(raw.readerIndex(), rawArray);

            final BridgeProto.Bridge bridge = BridgeProto.Bridge.parseFrom(rawArray);

            switch (bridge.getStatus()){
                case ONLINE:
                    channelIdMap.put(bridge.getDescription(), me.id());
                    //add listener that when the channel is over,remove the relative channelId in the channelIdMap
                    me.closeFuture().addListener(new ChannelFutureListener() {
                        public void operationComplete(ChannelFuture future) throws Exception {
                            channelIdMap.remove(bridge.getDescription());
                        }
                    });
                    System.out.println(bridge.getDescription() + " online");

                    //返回成功报文
                    BridgeProto.Bridge success = BridgeProto.Bridge.newBuilder()
                            .setStatus(BridgeProto.Bridge.Status.SUCCESS)
                            .setDescription("online succeed")
                            .build();
                    byte[] byteArray = success.toByteArray();
                    ByteBuf directBuf = ctx.alloc().directBuffer(byteArray.length, byteArray.length).writeBytes(byteArray);
                    ctx.writeAndFlush(new TextWebSocketFrame(directBuf));
                    break;
                case OFFLINE:
                    //you'd better remove the channelId in the channelIdMap when the group remove the inactive channel,
                    //as you can see above:we add a listener to the channel.
                    break;
                default:
                    break;
            }
        }
        //Exception：Method threw 'java.lang.NullPointerException' exception.
        // Cannot evaluate io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame.toString()
        if (msg instanceof BinaryWebSocketFrame){
            ByteBuf raw = ((BinaryWebSocketFrame) msg).content();
            int length = raw.readableBytes();
            byte[] rawArray = new byte[length];
            raw.getBytes(raw.readerIndex(), rawArray);
            TalkProto.Talk from = TalkProto.Talk.parseFrom(rawArray);

            List<String> toList = from.getToList();
            //byte[] byteArray = (from.getFrom() + ":" + from.getMsg()).getBytes();
            //ByteBuf directBuf = ctx.alloc().directBuffer(byteArray.length, byteArray.length).writeBytes(byteArray);
            for (String to : toList){
                group.find(channelIdMap.get(to)).writeAndFlush((new BinaryWebSocketFrame(raw.slice().retain())));
            }
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Client " + ctx.channel() + " offline");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    /*测试*/

    /*@Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        System.out.println("registered");
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        super.channelUnregistered(ctx);
        System.out.println("unregistered");
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        System.out.println("active");
    }*/
}
