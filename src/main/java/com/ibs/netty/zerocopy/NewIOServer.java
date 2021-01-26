package com.ibs.netty.zerocopy;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * @Author: ivy
 * @Date: 2021/1/26 16:28
 */
// 服务器端
public class NewIOServer {
    public static void main(String[] args) throws IOException {
        InetSocketAddress address = new InetSocketAddress(7001);
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        ServerSocket socket = serverSocketChannel.socket();
        socket.bind(address);
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        while (true){
            SocketChannel socketChannel = serverSocketChannel.accept();
            int readcount = 0;
            while (-1!= readcount){
                readcount = socketChannel.read(byteBuffer);
            }
            // 将buffer的position设置为0，将mark这个标志作废
            // 用于下次进行读取钱进行位置指针的重置
            byteBuffer.rewind();

        }
    }
}
