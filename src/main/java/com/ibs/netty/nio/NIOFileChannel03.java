package com.ibs.netty.nio;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * 实现将一个文件file01.txt中的文件复制到当前目录下的2.txt中
 * 要求：只使用一个buffer
 * */
public class NIOFileChannel03 {
    public static void main(String[] args) throws IOException {
        FileInputStream fileInputStream = new FileInputStream("1.txt");
        FileChannel fileChannel01 = fileInputStream. getChannel();


        FileOutputStream fileOutputstream = new FileOutputStream("2. txt");
        FileChannel fileChanne102 = fileOutputstream. getChannel();


        ByteBuffer byteBuffer = ByteBuffer .allocate(512);

        while (true) {
            //循环读取
            //这里有一一个重要的操作，一定不要忘 了
            /*
                public final Buffer clear() {
                    position = 0;
                    limit = capacity;
                    mark = -1;
                    return this;
                }
            */
            byteBuffer .clear(); //清空buffer
            int read = fileChannel01.read(byteBuffer);
            if(read == -1) {
                //表示读完
                break ;
            }
            //将buffer中的数据写入到fileChannel02 -- 2. txt
            byteBuffer . flip();
            fileChanne102.write(byteBuffer);
        }
        fileInputStream.close();
        fileOutputstream.close();









//        // 先从一个文件中读
//        File file = new File("d:\\file01.txt");
//        FileInputStream fileInputStream = new FileInputStream(file);
//        FileChannel inputStreamChannel = fileInputStream.getChannel();
//        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
//        // 把channel中的数据读出来，然后写入到buffer中
//        inputStreamChannel.read(byteBuffer);
//        fileInputStream.close();
//        // 重置buffer中的position指针
//        byteBuffer.flip();
//        byteBuffer.clear();
//
//        // 再 往一个文件里面写
//        FileOutputStream fileOutputStream = new FileOutputStream("d:\\file02.txt");
//        FileChannel outputStreamChannel = fileOutputStream.getChannel();
//        //从buffer中的数据放到channel中
//        outputStreamChannel.write(byteBuffer);
//        fileOutputStream.close();


    }
}
