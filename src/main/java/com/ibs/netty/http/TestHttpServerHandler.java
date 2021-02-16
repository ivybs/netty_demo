package com.ibs.netty.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import java.net.URI;

/**
 * @Author: ivy
 * @Date: 2021/2/16 15:45
 */
/**
 * 说明：
 * 1.SimpleChannelInboundHandler是ChannelInboundHandlerAdapter
 * 2.HttpObject表示客户端和服务器端相互通讯的数据被封装成HttpObject了
 * */
public class TestHttpServerHandler extends SimpleChannelInboundHandler<HttpObject> {
    // channelRead0 读取客户端数据
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, HttpObject httpObject) throws Exception {
        System.out.println("对应的channel=" + channelHandlerContext.channel() + " pipeline=" + channelHandlerContext
                .pipeline() + " 通过pipeline获取channel" + channelHandlerContext.pipeline().channel());

        System.out.println("当前ctx的handler=" + channelHandlerContext.handler());
        // 判断httpObject是不是http request请求
        if (httpObject instanceof HttpRequest){
            System.out.println("httpObject 类型： " + httpObject.getClass());
            System.out.println("客户端地址：" + channelHandlerContext.channel().remoteAddress());
            // 获取到
            HttpRequest httpRequest = (HttpRequest) httpObject;
            //获取uri
            URI uri = new URI(httpRequest.uri());
            if ("/favicon.ico".equals(uri.getPath())){
                System.out.println("你请求了/favicon.ico，不做响应");
                return;

            }

            // 回复信息给浏览器 http协议
            ByteBuf content = Unpooled.copiedBuffer("hello, im ur server", CharsetUtil.UTF_8);
            // 构造一个http的响应，即http respose
            FullHttpResponse defaultFullHttpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK,content);
            defaultFullHttpResponse.headers().set(HttpHeaderNames.CONTENT_TYPE,"text/plain");
            defaultFullHttpResponse.headers().set(HttpHeaderNames.CONTENT_LENGTH,content.readableBytes());
            //将构建好的defaultFullHttpResponse进行返回
            channelHandlerContext.writeAndFlush(defaultFullHttpResponse);

        }

    }
}
