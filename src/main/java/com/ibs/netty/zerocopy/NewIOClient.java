package com.ibs.netty.zerocopy;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;

/**
 * @Author: ivy
 * @Date: 2021/1/26 16:28
 */
// 客户端
public class NewIOClient {
    public static void main(String[] args) throws IOException {
        SocketChannel open = SocketChannel.open();
        open.connect(new InetSocketAddress("localhost",7001));
        String filename = "1.txt";
        // 得到一个文件channel
        FileChannel channel = new FileInputStream(filename).getChannel();
        // 准备发送
        long startTime = System.currentTimeMillis();
        // 在linux下一个transferTo方法就可以完成传输
        // 在window下，一次调用transforTo只能发送8m文件
        // 就需要分段传输文件，而且要明确分段位置
        long transferCount = channel.transferTo(0,channel.size(),open);
        System.out.println("发送的总字节数 = "+ transferCount +" 耗时 ：" + (System.currentTimeMillis()-startTime));
        // 关闭
        channel.close();
    }
}
