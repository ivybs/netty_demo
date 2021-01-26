package com.ibs.netty.nio;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/*
*说明
* 1. MappedByteBuffer可让文件直接在内存(堆外内存)修改，操作系统不需要拷贝1次
**/
public class MappedByteBufferTest {
    public static void main(String[] args) throws IOException {
        RandomAccessFile randomAccessFile = new RandomAccessFile("1.txt", "rw");
        //获取对应的通道
        FileChannel channel = randomAccessFile. getChannel();
        /**
         * 参数1: FileChannel .MapMode. READ_ _WRITE 使用的读写模式
         * 参数2: 0 :可以直接修改的起始位置
         * 参数3: 5:是映射到内存的大小（不是索引） 在这里就是最多可以映射5个字节 即将1.txt文件中的多少个字节映射到内存
         * 可以直接修改的范围就是0-5  不包含5
         * */
        MappedByteBuffer map = channel.map(FileChannel.MapMode.READ_WRITE, 0, 5);

        map.put(0, (byte) 'H');
        map.put(3,(byte) '9');
        map.put(5,(byte) '9');//IndexOutofBounds Exception
        randomAccessFile.close();
        System.out.println("修改成功");


    }
}
