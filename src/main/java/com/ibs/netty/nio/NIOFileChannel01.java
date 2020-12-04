package com.ibs.netty.nio;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * 将程序中的内容，写到文件中
 * */
public class NIOFileChannel01  {
    public static void main(String[] args) throws IOException {
        String str = "hello";
        // 新建个流 因为流是被包在channel里面的
        FileOutputStream fileOutputStream = new FileOutputStream("d:\\file01.txt");
        //通过这个流来新建channel
        FileChannel fileChannel = fileOutputStream.getChannel();
        //新建个buffer,并且为bytebuffer分配内存空间
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        //把数据放入buffer中
        byteBuffer.put(str.getBytes());

        // 重要！！！不要忘记把buffer给flip一下
        // 因为刚才是往buffer里面写数据，现在需要把buffer里面的数据读出来
        // 就需要去重置position指针来移到开始的位置进行数据的一个一个读取
        byteBuffer.flip();


        // 把buffer放入channel中，注意channel的读写方向是对于channel来说的
        // 所以将buffer放入channel中，是对channel进行写操作
        fileChannel.write(byteBuffer);
        //现在都放好了，就直接可以关闭
        fileOutputStream.close();
    }
}
