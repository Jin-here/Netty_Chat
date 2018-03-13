package com.vgaw.netty.server;

import com.vgaw.netty.initializer.ChatServerInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.ImmediateEventExecutor;

/**
 * Created by Administrator on 2015/8/31.
 */
public class ChatServer {
    /*
    * A thread-safe Set that contains open Channels and provides various bulk operations on them.
    * Using ChannelGroup, you can categorize Channels into a meaningful group (e.g. on a per-service
    * or per-state basis.) A closed Channel is automatically removed from the collection, so that you
    * don't need to worry about the life cycle of the added Channel. A Channel can belong to more
    * than one ChannelGroup.
    * */
    private static ChannelGroup channelGroup = new DefaultChannelGroup(ImmediateEventExecutor.INSTANCE);

    public void start(int port){
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try{
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChatServerInitializer(channelGroup));

            ChannelFuture future = bootstrap.bind(port).sync();

            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception{
        int port = 7777;
        ChatServer server = new ChatServer();
        server.start(port);
    }
}
