package com.ibs.netty.http;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * @Author: ivy
 * @Date: 2021/2/16 15:44
 */
public class TestServer {
    public static void main(String[] args) {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGraoup = new NioEventLoopGroup();

        try{
            // 创建服务器端的启动对象，进行参数的配置
            ServerBootstrap bootstrap = new ServerBootstrap();

            bootstrap.group(bossGroup,workerGraoup)//设置两个事件循环组
                    .channel(NioServerSocketChannel.class)// 使用NioSocketChannel作为服务器的通道实现
                    .option(ChannelOption.SO_BACKLOG,128) // 设置线程队列得到连接个数
                    .childOption(ChannelOption.SO_KEEPALIVE,true) //设置保持活动连接状态
                    .childHandler(new TestServerInitializer());// 给worker group的eventloop对应的管道设置处理器

            System.out.println("服务器准备好了");
            // 绑定端口，并且异步，生成了一个future对象
            // 启动服务器并绑定
            ChannelFuture sync = bootstrap.bind(8081).sync();
            // 对关闭通道进行监听
            sync.channel().closeFuture().sync();

        }catch(Exception e){
            e.printStackTrace();
        }finally{
            bossGroup.shutdownGracefully();
            workerGraoup.shutdownGracefully();
        }
    }
}
