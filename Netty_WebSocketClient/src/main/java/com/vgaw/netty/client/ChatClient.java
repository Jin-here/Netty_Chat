package com.vgaw.netty.client;

        import com.vgaw.netty.initializer.ChatClientInitializer;
        import com.vgaw.netty.pojo.TalkProto;
        import io.netty.bootstrap.Bootstrap;
        import io.netty.buffer.ByteBuf;
        import io.netty.buffer.Unpooled;
        import io.netty.channel.*;
        import io.netty.channel.nio.NioEventLoopGroup;
        import io.netty.channel.socket.nio.NioSocketChannel;
        import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
        import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;

        import java.io.BufferedReader;
        import java.io.InputStreamReader;
        import java.net.URI;
        import java.util.ArrayList;
        import java.util.List;

/**
 * Created by Administrator on 2015/9/1.
 */
public class ChatClient {
    private final URI webSocketURI;

    public ChatClient(URI webSocketURI){
        this.webSocketURI = webSocketURI;
    }

    public void run(){
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            final Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChatClientInitializer(this.webSocketURI));

            //�����첽���Ӳ���
            Channel channel = bootstrap.connect(this.webSocketURI.getHost(), this.webSocketURI.getPort()).sync().channel();

            //expected: ByteBuf, FileRegion
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            while (true){
                String msg = in.readLine();
                if (msg.equalsIgnoreCase("bye")){
                    System.out.println("The Game Is Over,See You Again!");
                    break;
                }
                char[] rawC = msg.toCharArray();
                int i = 2;
                List<String> toList = new ArrayList<String>();
                while (rawC[i] != 32 && i < msg.length()){
                    toList.add(msg.substring(i,i+2));
                    i += 2;
                }
                //--------------------------------//
                //-���磺0103 hello---------------//
                //--from---01---�Լ���ʶ----------//
                //---to----03---Ŀ���ʶ(�ɶ��)--//
                //--talk-hello--�Ի�����----------//
                //-------------------------------//
                //channel.writeAndFlush(new TextWebSocketFrame(msg));
                TalkProto.Talk talk = TalkProto.Talk.newBuilder()
                        .setFrom(msg.substring(0, 2)).addAllTo(toList).setMsg(msg.substring(i+1))
                        .build();
                channel.writeAndFlush(talk);
            }

            //����CloseWebSocketFrame
            //�ر����̣�����������͹ر�֡�����������عر�֡���ͻ��˵���ctx.close()
            //�ر�channel
            //���ܴ������⣺����������͹ر�֡���������첽�ģ����ܻ�û����ctx.close()��
            //���Ѿ�ִ�е�group.shutdownGracefully();
            channel.writeAndFlush(new CloseWebSocketFrame());

            //�ȴ��ͻ�����·�ر�
            //channel.closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            group.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception{
        URI uri = new URI("ws://127.0.0.1:7777/websocket");
        new ChatClient(uri).run();
    }
}
