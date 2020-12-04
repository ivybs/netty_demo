package com.ibs.netty.nio;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * 将文件中的内容读到程序中
 * */
public class NIOFileChannel02 {
    public static void main(String[] args) throws IOException {
        //创建文件
        File file = new File("d:\\file01.txt");

        //对文件中的数据进行读取到程序中
        FileInputStream fileInputStream = new FileInputStream(file);
        // 通过流来获取通道
        FileChannel inputStreamChannel = fileInputStream.getChannel();

        // 创建buffer，分配内存空间
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        //将channel中的数据读取出来，并且写入到buffer中
        inputStreamChannel.read(byteBuffer);

        System.out.println(new String(byteBuffer.array()));
        fileInputStream.close();

    }
}
