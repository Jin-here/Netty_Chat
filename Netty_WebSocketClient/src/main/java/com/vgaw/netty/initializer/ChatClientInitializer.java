package com.vgaw.netty.initializer;

import com.vgaw.netty.channelhandler.BinaryFrameToTalkDecoder;
import com.vgaw.netty.channelhandler.TalkHandler;
import com.vgaw.netty.channelhandler.TalkToBinaryFrameEncoder;
import com.vgaw.netty.channelhandler.WebSocketHandShakeNextHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.WebSocketClientProtocolHandler;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.stream.ChunkedWriteHandler;

import java.net.URI;
import java.net.URL;

import static io.netty.handler.codec.http.websocketx.WebSocketVersion.*;

/**
 * Created by Administrator on 2015/9/1.
 */
public class ChatClientInitializer extends ChannelInitializer<Channel> {
    private final URI webSocketURI;

    public ChatClientInitializer(URI webSocketURI){
        this.webSocketURI = webSocketURI;
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        pipeline.addLast(new HttpClientCodec());
        pipeline.addLast(new HttpObjectAggregator(64 * 1024));
        //pipeline.addLast(new ChunkedWriteHandler());

        /*
        * This handler does all the heavy lifting for you to run a
        * websocket client. It takes care of websocket handshaking as well
        * as processing of Ping, Pong frames. Text and Binary data frames are
        * passed to the next handler in the pipeline (implemented by you) for
        * processing. Also the close frame is passed to the next handler as you
        * may want inspect it before close the connection if the handleCloseFrames
        * is false, default is true. This implementation will establish the websocket
        * connection once the connection to the remote server was complete. To know
        * once a handshake was done you can intercept the
        * ChannelHandler.userEventTriggered(ChannelHandlerContext, Object) and
        * check if the event was of type
        * WebSocketClientProtocolHandler.ClientHandshakeStateEvent.HANDSHAKE_ISSUED or
        * WebSocketClientProtocolHandler.ClientHandshakeStateEvent.HANDSHAKE_COMPLETE.
        * */
        pipeline.addLast(new WebSocketClientProtocolHandler(webSocketURI,
                V13, null, false, new DefaultHttpHeaders(),
                64 * 1024));

        pipeline.addLast(new WebSocketHandShakeNextHandler());
        pipeline.addLast(new BinaryFrameToTalkDecoder());
        pipeline.addLast(new TalkHandler());
        pipeline.addLast(new TalkToBinaryFrameEncoder());
    }
}
