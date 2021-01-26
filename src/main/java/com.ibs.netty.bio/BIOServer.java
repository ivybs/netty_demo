package com.ibs.netty.bio;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BIOServer {
    public static void main(String[] args) throws IOException {
        //线程池机制

        //思路
        // 1.创建一个线程池
        ExecutorService newCacheThreadPool = Executors.newCachedThreadPool();

        // 2.如果有客户端连接，就创建一个线程，与之通信（单独写一个方法）
        ServerSocket serverSocket = new ServerSocket(6666);
        System.out.println("服务器启动了");
        while (true){
            System.out.println("等待连接");
            //监听，等待客户端连接
            final Socket socket = serverSocket.accept();
            System.out.println("连接到一个客户端");
            //创建一个线程，与之通讯（单独写一个方法）
            newCacheThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    // 重写run方法，可以和客户端进行通讯
                    handler(socket);
                }
            });
        }
    }

    public static void handler(Socket socket){
        byte[] bytes = new byte[1024];
        // 通过sockt 获取输入流
        try {
            InputStream inputStream = socket.getInputStream();
            //循环读取客户端发送的数据
            while (true){
                System.out.println("线程名"+Thread.currentThread().getName()+"线程id"+Thread.currentThread().getId());
                System.out.println("read...");
                int read = inputStream.read(bytes);
                if (read != -1){
                    //输出客户端发送的数据
                                      System.out.println(read);
                } else {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            System.out.println("关闭和client的连接");
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
