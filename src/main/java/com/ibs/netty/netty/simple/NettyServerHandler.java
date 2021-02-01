package com.ibs.netty.netty.simple;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

import java.nio.ByteBuffer;

/**
 * @Author: ivy
 * @Date: 2021/2/1 16:33
 */
/**
 * 说明：
 * 1. 我们自定义的handler需要继承netty规定好的handlerAdapter(规定好的规范)
 * 2.这时我们自定义的handler才能称作式一个handler
 *
 * */
public class NettyServerHandler extends ChannelInboundHandlerAdapter {
    // 读取数据的事件
    // 这里可以读取客户端发送来的消息
    /**
     * 1.ChannelHandlerContext 上下文对象，含有管道pipline（业务逻辑处理）,通道（数据），地址
     * 2.Object 就是客户端发送的数据
     * */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        System.out.println("server ctx = " + ctx);
        // 将msg转成一个ByteBuf
        // ByteBuf是netty提供的，不是NIO的ByteBuffer（性能更好）
        ByteBuf buf = (ByteBuf) msg;
        System.out.println("客户端说：" + buf.toString(CharsetUtil.UTF_8));
        System.out.println("客户端地址是：" + ctx.channel().remoteAddress());
    }

    // 数据读取完毕，并向客户端回一个消息
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {

        // write:把数据写到缓冲区，flush:将缓冲区的数据写到通道里
        // 一般来讲，我们对发送的数据进行编码
        ctx.writeAndFlush(Unpooled.copiedBuffer("hello,客户端~",CharsetUtil.UTF_8));
    }

    //处理异常，一般是需要关闭通道
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}
