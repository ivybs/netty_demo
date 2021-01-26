package com.ibs.netty.nio;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

// 从一个通道将数据拷贝到另外一个通道
public class NIOFileChannel04 {
    public static void main(String[] args) throws IOException {
        //创建相关流
        FileInputStream fileInputStream = new FileInputStream( "d:\\a.jpg");
        FileOutputStream fileOutputstream = new FileOutputStream("d: \\a2.jpg");
        //获取各个流对应的filechannel
        FileChannel sourceCh = fileInputStream. getChannel();
        FileChannel destCh = fileOutputstream. getChannel();
        //使用transferForm完成拷贝
        destCh. transferFrom( sourceCh, 0, sourceCh.size());
        //关闭相关通道和流
        sourceCh.close();
        destCh.close( );
        fileInputStream. close();
        fileOutputstream. close();


    }
}
