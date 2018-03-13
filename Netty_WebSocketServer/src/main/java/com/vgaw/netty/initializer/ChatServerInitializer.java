package com.vgaw.netty.initializer;

import com.vgaw.netty.channelhandler.WebSocketHandShakeNextHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;

/**
 * Created by Administrator on 2015/8/31.
 */
public class ChatServerInitializer extends ChannelInitializer<Channel> {
    private final ChannelGroup group;

    public ChatServerInitializer(ChannelGroup group) {
        this.group = group;
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        //Decode bytes to HttpRequest, HttpContent,LastHttpContent.
        //Encode HttpResponse,HttpContent,LastHttpContent to bytes
        pipeline.addLast(new HttpServerCodec());


        //This ChannelHandler aggregates an HttpMessage and its following
        //HttpContents into a single FullHttpRequest or FullHttpResponse(
        // depending on whether it is being used to handle requests or
        // responses).With this installed the next ChannelHandler in the pipeline
        //will receive only full HTTP requests
        pipeline.addLast(new HttpObjectAggregator(64 * 1024));


        //add support for large data writing
        //pipeline.addLast(new ChunkedWriteHandler());
        //pipeline.addLast(new HttpRequestHandler("/websocket"));


        //As required by the WebSockets specification,handle the WebSocket Upgrade
        //handshake,PingWebSocketFrames,PongWebSocketFrames and CloseWebSocketFrames
        /*
        * This handler does all the heavy lifting for you to run a websocket server.
        * It takes care of websocket handshaking as well as processing of control frames (Close, Ping, Pong).
        * Text and Binary data frames are passed to the next handler in the pipeline (implemented by you) for processing.
        * See io.netty.example.http.websocketx.html5.WebSocketServer for usage. The implementation of this handler
        * assumes that you just want to run a websocket server and not process other types HTTP requests (like GET and POST).
        * If you wish to support both HTTP requests and websockets in the one server, refer to the
        * io.netty.example.http.websocketx.server.WebSocketServer example. To know once a handshake was done you can
        * intercept the ChannelHandler.userEventTriggered(ChannelHandlerContext, Object) and check if the event was
        * of type WebSocketServerProtocolHandler.ServerHandshakeStateEvent.HANDSHAKE_COMPLETE.
        * */
        pipeline.addLast(new WebSocketServerProtocolHandler("/websocket"));
        //Handles TextWebSocketFrames and handshake completion events
        pipeline.addLast(new WebSocketHandShakeNextHandler(group));
    }
}
