package com.vgaw.netty.channelhandler;

import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.stream.ChunkedNioFile;

import java.io.File;
import java.io.RandomAccessFile;
import java.net.URISyntaxException;
import java.net.URL;

import static io.netty.handler.codec.http.HttpHeaderNames.*;
import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static io.netty.handler.codec.http.HttpVersion.*;

/**
 * Created by Administrator on 2015/8/30.
 */
public class HttpRequestHandler extends SimpleChannelInboundHandler<FullHttpRequest>{
    private final String wsUri;
    /*private static final File INDEX;

    static {
        URL location = HttpRequestHandler.class.getProtectionDomain().getCodeSource().getLocation();
        try{
            String path = location.toURI() + "index.html";
            path = !path.contains("file:") ? path : path.substring(5);
            INDEX = new File(path);
        }catch (URISyntaxException e){
            throw new IllegalStateException("Unable to locate index.html", e);
        }
    }*/

    public HttpRequestHandler(String wsUri){
        this.wsUri = wsUri;
    }

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {
        //如果请求是一次升级了的WebSocket请求，则递增引用计数器（retain）并且将它传递
        //给在ChannelPipeline中的下个ChannleInboundHandler
        if (wsUri.equalsIgnoreCase(msg.uri())){
            ctx.fireChannelRead(msg.retain());
        }/*else {
            //如果客户端发送的HTTP1.1头是"Expect:100-continue"
            //则发送"100 Continue"的响应
            if (HttpHeaderUtil.is100ContinueExpected(msg)){
                send100Continue(ctx);
            }

            //读取index.html
            RandomAccessFile file = new RandomAccessFile(INDEX, "r");

            HttpResponse response = new DefaultHttpResponse(msg.protocolVersion(), HttpResponseStatus.OK);

            response.headers().set(CONTENT_TYPE, "text/html; charset=UTF-8");

            boolean keepAlive = HttpHeaderUtil.isKeepAlive(msg);

            if (keepAlive){
                HttpHeaderUtil.setContentLength(msg, file.length());
                HttpHeaderUtil.setKeepAlive(msg, true);
            }
            //在头被设置后，写一个HttpReponse返回给客户端。注意，这不是FullHttpResponse，
            //这只是响应的第一部分。另外，这里我们也不使用writeAndFlush()，
            //这个是留在最后完成。
            ctx.write(response);

            //如果传输过程没有要求加密也没有要求压缩，那么把index.html的内容存储在一个
            //DefaultFileRegion里就可以达到最好的效率。这将利用零拷贝来执行传输。
            //处于这个原因，我们要检查ChannelPipeline中是否有一个SslHandler。如果是的
            //话，我们就使用ChunkedNioFile。
            if (ctx.pipeline().get(SslHandler.class) == null){
                ctx.write(new DefaultFileRegion(file.getChannel(), 0, file.length()));
            }else {
                ctx.write(new ChunkedNioFile(file.getChannel()));
            }

            //写LastHttpContent来标记响应的结束，并终止他
            //LastHttpContent标记是HTTP request的结束，同时可能包含“头的尾部信息”
            //LastHttpContent.EMPTY_LAST_CONTENT为"end of content"，没有包含“头的尾部信息”
            ChannelFuture future = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
            //如果不要求keepalive，添加ChannelFutureListener到ChannelFuture对象的最后写入，并关闭连接。
            if (!keepAlive){
                future.addListener(ChannelFutureListener.CLOSE);
            }
        }*/

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    private void send100Continue(ChannelHandlerContext ctx) {
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, CONTINUE);
        ctx.writeAndFlush(response);
    }
}
