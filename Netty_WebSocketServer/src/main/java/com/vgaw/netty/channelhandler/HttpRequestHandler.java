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
        //���������һ�������˵�WebSocket������������ü�������retain�����ҽ�������
        //����ChannelPipeline�е��¸�ChannleInboundHandler
        if (wsUri.equalsIgnoreCase(msg.uri())){
            ctx.fireChannelRead(msg.retain());
        }/*else {
            //����ͻ��˷��͵�HTTP1.1ͷ��"Expect:100-continue"
            //����"100 Continue"����Ӧ
            if (HttpHeaderUtil.is100ContinueExpected(msg)){
                send100Continue(ctx);
            }

            //��ȡindex.html
            RandomAccessFile file = new RandomAccessFile(INDEX, "r");

            HttpResponse response = new DefaultHttpResponse(msg.protocolVersion(), HttpResponseStatus.OK);

            response.headers().set(CONTENT_TYPE, "text/html; charset=UTF-8");

            boolean keepAlive = HttpHeaderUtil.isKeepAlive(msg);

            if (keepAlive){
                HttpHeaderUtil.setContentLength(msg, file.length());
                HttpHeaderUtil.setKeepAlive(msg, true);
            }
            //��ͷ�����ú�дһ��HttpReponse���ظ��ͻ��ˡ�ע�⣬�ⲻ��FullHttpResponse��
            //��ֻ����Ӧ�ĵ�һ���֡����⣬��������Ҳ��ʹ��writeAndFlush()��
            //��������������ɡ�
            ctx.write(response);

            //����������û��Ҫ�����Ҳû��Ҫ��ѹ������ô��index.html�����ݴ洢��һ��
            //DefaultFileRegion��Ϳ��Դﵽ��õ�Ч�ʡ��⽫�����㿽����ִ�д��䡣
            //�������ԭ������Ҫ���ChannelPipeline���Ƿ���һ��SslHandler������ǵ�
            //�������Ǿ�ʹ��ChunkedNioFile��
            if (ctx.pipeline().get(SslHandler.class) == null){
                ctx.write(new DefaultFileRegion(file.getChannel(), 0, file.length()));
            }else {
                ctx.write(new ChunkedNioFile(file.getChannel()));
            }

            //дLastHttpContent�������Ӧ�Ľ���������ֹ��
            //LastHttpContent�����HTTP request�Ľ�����ͬʱ���ܰ�����ͷ��β����Ϣ��
            //LastHttpContent.EMPTY_LAST_CONTENTΪ"end of content"��û�а�����ͷ��β����Ϣ��
            ChannelFuture future = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
            //�����Ҫ��keepalive�����ChannelFutureListener��ChannelFuture��������д�룬���ر����ӡ�
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
