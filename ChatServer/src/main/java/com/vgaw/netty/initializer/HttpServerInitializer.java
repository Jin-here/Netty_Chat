package com.vgaw.netty.initializer;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import com.vgaw.netty.channlehandler.HttpContentHandler;

/**
 * Created by Administrator on 2015/9/9.
 */
public class HttpServerInitializer extends ChannelInitializer<Channel> {
    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        pipeline.addLast(new HttpServerCodec());
        // belong to encoder
        // Compresses an HttpMessage and an HttpContent in gzip or deflate
        // encoding while respecting the "Accept-Encoding" header. If there
        // is no matching encoding, no compression is done. For more information
        // on how this handler modifies the message, please refer to HttpContentEncoder.

        //only compress the HttpContent,so the final msg is like:
        //HttpMessage(HttpResponse or HttpRequest) + content-length + HttpContent(compressed if has compressor)

        //question:how to delimit
        //pipeline.addLast(new HttpContentCompressor());
        //belong to decoder
        pipeline.addLast(new HttpObjectAggregator(64 * 1024));

        pipeline.addLast(new HttpContentHandler());
    }
}
