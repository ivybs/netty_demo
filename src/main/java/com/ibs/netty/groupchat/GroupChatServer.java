package com.ibs.netty.groupchat;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
/**
 * 1.先编写服务器
 * 1.1 服务器启动并监听6667
 * 1.2 服务器接收客户端信息，并实现转发 【处理上线和下线】
 * */
public class GroupChatServer {
    // 定义属性
    private Selector selector;
    // ServerSocketChannel 在服务器端监听新的客户端 Socket 连接
    // SocketChannel，网络 IO 通道，具体负责进行读写操作。NIO 把缓冲区的数据写入通道，或者把通道里的数据读到缓冲区。
    private ServerSocketChannel listener;
    private static  final int PORT = 6667;
    // 构造器 进行初始化
    public GroupChatServer(){
        try{
            // 得到选择器
            selector = Selector.open();
            // ServerSocketChannel
            listener = ServerSocketChannel.open();
            // 绑定端口
            listener.socket().bind(new InetSocketAddress(PORT));
            // 设置非阻塞模式
            listener.configureBlocking(false);
            //把listener注册到selector
            listener.register(selector, SelectionKey.OP_ACCEPT);
        }catch(Exception e){
            e.printStackTrace();
        }finally{

        }
    }
    // 监听
    public void listen(){
        try{
            // 循环处理
            while (true){
                // 等待2秒
                int count = selector.select();
                // 有事件需要处理
                if (count > 0){
                    // 遍历得到selectionKey集合
                    Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                    while (iterator.hasNext()){
                        //取出selectionKey
                        SelectionKey key = iterator.next();
                        // 监听到连接事件 上线
                        if (key.isAcceptable()){
                            SocketChannel socketChannel = listener.accept();
                            // 设置非阻塞
                            socketChannel.configureBlocking(false);
                            //将socketChannel注册到selector
                            socketChannel.register(selector,SelectionKey.OP_READ);

                            //进行提示
                            System.out.println(socketChannel.getLocalAddress() + "上线");
                        }
                        // 通道发生了read事件，即通道是可读的状态
                        // 就要从通道里面读到buffer里面去
                        // 从客户端读取需要群发的数据
                        if (key.isReadable()){
                            // 处理读
                            readData(key);

                        }
                        // 当前的key的删除，防止重复操作
                        // 即手动的从集合中以懂当前的selectionKey，防止重复操作
                        iterator.remove();

                    }

                } else{
                    System.out.println("等待");
                }
            }

        }catch(Exception e){
            e.printStackTrace();
        }finally{

        }

    }

    // 读取客户端消息
    private void readData(SelectionKey selectionKey){
        // 定义一个socketChannel
        SocketChannel socketChannel = null;
        try{

            socketChannel = (SocketChannel)selectionKey.channel();
            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
            // count记录的是读取到的数据长度
            int count = socketChannel.read(byteBuffer);
            // 根据count的值做处理
            if (count > 0){
                // 把缓冲区的数据转成字符串
                String s = new String(byteBuffer.array());
                System.out.println("from 客户端：" + s);
                // 向其他的客户端转发消息(去掉自己) 专门写一个方法来处理
                sendInfoToOtherClient(s,socketChannel);
            }


        }catch(IOException e){
            try {
                System.out.println(socketChannel.getRemoteAddress() + "离线了");
                // 取消注册
                selectionKey.cancel();
                // 关闭通道
                socketChannel.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }finally{

        }

    }

    // 转发消息给其他的客户（通道，因为一个客户就对应一个通道）
    private void sendInfoToOtherClient(String msg,SocketChannel self) throws IOException {
        System.out.println("服务器转发消息中");
        // 遍历所有注册到selector上的socketChannel并且排除自己
        for (SelectionKey key: selector.keys()){
            // 通过key 取出对应的SocketChannel
            Channel targetChannel = key.channel();
            // 排除自己
            if (targetChannel instanceof SocketChannel && targetChannel != self){
                // 转型
                SocketChannel dest = (SocketChannel) targetChannel;
                // 将msg存储到buffer
                ByteBuffer buffer = ByteBuffer.wrap(msg.getBytes());
                // 将buffer数据写入到通道中
                dest.write(buffer);
            }
        }
    }

    public static void main(String[] args) {
        //创建一个服务器对象
        GroupChatServer groupChatServer = new GroupChatServer();
        groupChatServer.listen();
    }
}
