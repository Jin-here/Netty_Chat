package com.vgaw.netty.channlehandler;

import com.vgaw.hibernate.dao.FriendDao;
import com.vgaw.hibernate.dao.UserDao;
import com.vgaw.hibernate.pojo.Friend;
import com.vgaw.hibernate.pojo.User;
import com.vgaw.netty.protopojo.FlyCatProto;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;

import java.util.ArrayList;

import static io.netty.handler.codec.http.HttpHeaderNames.*;
import static io.netty.handler.codec.http.HttpResponseStatus.CONTINUE;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * Created by Administrator on 2015/9/9.
 */
public class HttpContentHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {
        if (HttpHeaderUtil.is100ContinueExpected(msg)) {
            ctx.write(new DefaultFullHttpResponse(HTTP_1_1, CONTINUE));
        }
        boolean keepAlive = HttpHeaderUtil.isKeepAlive(msg);

        ByteBuf raw = msg.content();
        byte[] rawArray = new byte[raw.readableBytes()];
        raw.getBytes(raw.readerIndex(), rawArray);

        FlyCatProto.FlyCat flyCat = FlyCatProto.FlyCat.parseFrom(rawArray);
        flyCat = separate(flyCat);

        FullHttpResponse response;
        if (flyCat != null) {
            byte[] byteArray = flyCat.toByteArray();
            ByteBuf directBuf = ctx.alloc().directBuffer(byteArray.length, byteArray.length).writeBytes(byteArray);
            response = new DefaultFullHttpResponse(HTTP_1_1, OK, directBuf);
            response.headers().set(CONTENT_TYPE, "text/plain");
            response.headers().setInt(CONTENT_LENGTH, response.content().readableBytes());
        } else {
            // no data to send back.
            response = new DefaultFullHttpResponse(HTTP_1_1, OK);
            response.headers().setInt(CONTENT_LENGTH, response.content().readableBytes());
        }

        if (!keepAlive) {
            //response.headers().set(CONNECTION, HttpHeaderValues.CLOSE);
            ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
        } else {
            response.headers().set(CONNECTION, HttpHeaderValues.KEEP_ALIVE);
            ctx.writeAndFlush(response);
        }


    }

    /**
     * Invoked when the last message read by the current read operation has been consumed by
     * {@link #channelRead(ChannelHandlerContext, Object)}.  If {@link ChannelOption#AUTO_READ} is off, no further
     * attempt to read an inbound data from the current {@link Channel} will be made until
     * {@link ChannelHandlerContext#read()} is called.
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    /**
     * separate the parcel flycats bring
     */
    private FlyCatProto.FlyCat separate(FlyCatProto.FlyCat flyCat) {
        UserDao userDao = new UserDao();
        FriendDao friendDao = new FriendDao();

        switch (flyCat.getFlag()) {
            case 1:
                // save user info.
                User user1 = new User();
                user1.setToken(flyCat.getStringV(0));
                user1.setName(flyCat.getStringV(1));
                user1.setPassword(flyCat.getStringV(2));
                userDao.saveUser(user1);
                return FlyCatProto.FlyCat.newBuilder().setFlag(1).build();
            case 2:
                // login
                User user2 = userDao.queryUserByName(flyCat.getStringV(0));
                if (user2 != null){
                    if (user2.getPassword().equals(flyCat.getStringV(1))){
                        return FlyCatProto.FlyCat.newBuilder().setFlag(1)
                                .addLongV(user2.getId())
                                .addStringV(user2.getToken())
                                .addStringV(user2.getName())
                                .addStringV(user2.getPassword()).build();
                    }
                }
                return FlyCatProto.FlyCat.newBuilder().setFlag(0).build();
            case 3:
                // change password

            case 5:
                // query whether the user is exist.
                User user5 = userDao.queryUserByName(flyCat.getStringV(0));
                if (user5 != null) {
                    // exist.
                    return FlyCatProto.FlyCat.newBuilder().setFlag(1).build();
                }else {
                    return FlyCatProto.FlyCat.newBuilder().setFlag(0).build();
                }
            case 6:
                Friend friend = new Friend();
                friend.setName(flyCat.getStringV(0));
                friend.setFriendName(flyCat.getStringV(1));
                friendDao.saveFriend(friend);
                return FlyCatProto.FlyCat.newBuilder().setFlag(1).build();
            case 7:
                ArrayList<String> friendList = friendDao.queryFriendByName(flyCat.getStringV(0));
                FlyCatProto.FlyCat.Builder builder = FlyCatProto.FlyCat.newBuilder().setFlag(1);
                for (String friend1 : friendList){
                    builder.addStringV(friend1);
                }
                return builder.build();
        }
        return null;

    }

}
