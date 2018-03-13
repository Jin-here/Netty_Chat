package com.vgaw.netty.server;

import com.vgaw.hibernate.util.HibernateUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import com.vgaw.netty.initializer.HttpServerInitializer;

/**
 * Created by Administrator on 2015/9/9.
 */
public class HttpServer {
    public void start(int port){
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try{
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new HttpServerInitializer());

            ChannelFuture future = bootstrap.bind(port).sync();

            future.channel().closeFuture().sync();
        } catch (InterruptedException e){
            e.printStackTrace();
        }finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();

            HibernateUtil.getSessionFactory().close();
        }
    }

    public static void main(String[] args) throws Exception{
        int port = 7778;
        HttpServer server = new HttpServer();
        server.start(port);
    }
}
