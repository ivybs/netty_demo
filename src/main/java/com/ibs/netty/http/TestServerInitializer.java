package com.ibs.netty.http;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;

/**
 * @Author: ivy
 * @Date: 2021/2/16 15:45
 */
public class TestServerInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        //向管道加入处理器

        // 得到管道
        ChannelPipeline pipeline = socketChannel.pipeline();
        //加入一个netty提供的HttpServerCodec codec => [coder + decoder]
        //1. HttpServerCodec是Netty提供的编解码器
        pipeline.addLast("MyHttpServerCodec",new HttpServerCodec());
        //2.增加一个自定义的handler
        pipeline.addLast("MyHttpServerHandler",new TestHttpServerHandler());

        System.out.println("ok~~~ ");


    }

}
