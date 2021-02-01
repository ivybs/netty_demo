package com.ibs.netty.netty.simple;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * @Author: ivy
 * @Date: 2021/2/1 16:18
 */
public class NettyServer {
    public static void main(String[] args) throws InterruptedException {
        // 创建bossgroup 和 workgroup
        /**
         * 说明:
         * 1.创建了两个线程组
         * 2.bossGroup只处理连接请求，真正与客户端业务处理会交给workgroup来处理，两个都是无限循环
         *
         * */
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGraoup = new NioEventLoopGroup();
        try{
            // 创建服务器端的启动对象，进行参数的配置
            ServerBootstrap bootstrap = new ServerBootstrap();

            //使用链式编程来进行设置
            bootstrap.group(bossGroup,workerGraoup)//设置两个线程组
                    .channel(NioServerSocketChannel.class)// 使用NioSocketChannel作为服务器的通道实现
                    .option(ChannelOption.SO_BACKLOG,128) // 设置线程队列得到连接个数
                    .childOption(ChannelOption.SO_KEEPALIVE,true) //设置保持活动连接状态
                    .childHandler(new ChannelInitializer<SocketChannel>() {// 创建一个通道初始化对象
                        // 向pipline设置处理器
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new NettyServerHandler());
                        }

                    });// 给worker group的eventloop对应的管道设置处理器

            System.out.println("服务器准备好了");
            // 绑定端口，并且同步，生成了一个future对象
            // 启动服务器并绑定
            ChannelFuture sync = bootstrap.bind(6668).sync();

            // 对关闭通道进行监听
            sync.channel().closeFuture().sync();
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            // 关闭两个group
            bossGroup.shutdownGracefully();
            workerGraoup.shutdownGracefully();
        }



    }
}
