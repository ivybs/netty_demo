package com.ibs.netty.tcp;

import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.util.CharsetUtil;

import java.util.concurrent.TimeUnit;

/**
 * @Author: ivy
 * @Date: 2021/2/1 16:33
 */
/**
 * 说明：
 * 1. 我们自定义的handler需要继承netty规定好的handlerAdapter(规定好的规范)
 * 2.这时我们自定义的handler才能称作式一个handler
 *
 * 如果事件的运动方向是从客户端到服务端，那么称这个事件为出栈
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
        // 比如这里我们有一个非常耗时的业务 ==> 异步执行 ==> 提交到该channel对应的NIOEventLoop中的taskQueue中


        // 解决方案1：用户程序自定义的普通任务
        EventLoop eventExecutors = ctx.channel().eventLoop();
        // 将任务放到taskqueue里面
        // taskQueue里面 拿出来执行第一个
        eventExecutors.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(10*1000);
                    ctx.writeAndFlush(Unpooled.copiedBuffer("hello,客户端1",CharsetUtil.UTF_8));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        System.out.println("1执行完了");

        // taskQueue里面 拿出来执行第二个 因此虽然这个任务睡了20s，但是实际任务1用了10s，所以这里总共睡了30s
        // 这两个任务是在同一个线程
        eventExecutors.execute(new Runnable() {
            @Override
            public void run() {
                System.out.println("2开始执行");
                try {
                    Thread.sleep(20*1000);
                    ctx.writeAndFlush(Unpooled.copiedBuffer("hello,客户端2",CharsetUtil.UTF_8));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        System.out.println("2执行完了");
        //解决方法2：用户自定义定时任务 => 该任务是提交到scheduleTaskQueue里面的
        ctx.channel().eventLoop().schedule(new Runnable() {
            @Override
            public void run() {
                System.out.println("3开始执行");
                try {
                    Thread.sleep(20*1000);
                    ctx.writeAndFlush(Unpooled.copiedBuffer("hello,客户端3",CharsetUtil.UTF_8));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        },5, TimeUnit.SECONDS);

        System.out.println("go on");




//        System.out.println("服务器读取线程： " + Thread.currentThread().getName());
//        System.out.println("server ctx = " + ctx);
//        System.out.println("看看channel和pipeline的关系");// channel中包含pipeline;pipeline中也包含channel；一一对应关系
//        Channel channel = ctx.channel();
//        ChannelPipeline channelPipeline = ctx.pipeline();// 本质是一个双向链表，出队入队问题
//        // 将msg转成一个ByteBuf
//        // ByteBuf是netty提供的，不是NIO的ByteBuffer（性能更好）
//        ByteBuf buf = (ByteBuf) msg;
//        System.out.println("客户端说：" + buf.toString(CharsetUtil.UTF_8));
//        System.out.println("客户端地址是：" + ctx.channel().remoteAddress());
    }

    // 数据读取完毕，并向客户端回一个消息
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {

        // write:把数据写到缓冲区，flush:将缓冲区的数据写到通道里
        // 一般来讲，我们对发送的数据进行编码
        ctx.writeAndFlush(Unpooled.copiedBuffer("hello,客户端~ 数据读取完毕",CharsetUtil.UTF_8));
    }

    //处理异常，一般是需要关闭通道
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}
