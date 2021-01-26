package com.ibs.netty.groupchat;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Scanner;

/**
 * 2.编写客户端
 * 2.1 连接服务器
 * 2.2 发送消息
 * 2.3 接收服务器消息
 * */
public class GroupChatClient
{
    // 定义属性
    // 服务器ip
    private final String HOST = "127.0.0.1";
    //服务器端口
    private final int PORT = 6667;
    private Selector selector;
    private SocketChannel socketChannel;
    // 客户端的名字
    private String username;


    // 构造器 完成初始化操作 连接服务器
    public GroupChatClient() throws IOException {
        selector = Selector.open();
        //连接服务器
        socketChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1",PORT));
        // 设置为非阻塞
        socketChannel.configureBlocking(false);
        //将channel注册到selector 关注read事件
        socketChannel.register(selector, SelectionKey.OP_READ);
        //得到username
        username = socketChannel.getLocalAddress().toString().substring(1);
        System.out.println(username + "  is ok...");
    }

    // 向服务器发送消息
    public void sendInfo(String info){
        info = username + "says : " + info;
        try {
            socketChannel.write(ByteBuffer.wrap(info.getBytes()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // 读取从服务器端回复的消息
    public void readInfo() throws IOException {
        int readChannels = 0;
        try {
            // 如果没有事件发生会一直阻塞在这里
            readChannels = selector.select();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 有读事件发生的通道
        if (readChannels > 0 ){
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while(iterator.hasNext()){
                SelectionKey key = iterator.next();
                if (key.isReadable()){
                    // 得到相关通道
                    SocketChannel channel = (SocketChannel) key.channel();
                    // 得到一个buffer
                    ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                    channel.read(byteBuffer);
                    String msg = new String(byteBuffer.array());
                    System.out.println(msg.trim());
                }
                // 已经执行过的事件要进行移除
                iterator.remove();
            }
        }else{

        }
    }


    public static void main(String[] args) throws IOException {
        //启动客户端
        GroupChatClient groupChatClient = new GroupChatClient();
        //启动一个线程,每隔3s读取从服务器端发送的数据
        new Thread(() -> {
                while (true){

                    try {
                        groupChatClient.readInfo();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    try {
                        Thread.currentThread().sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
        },"A").start();

        // 发送数据给客户端
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()){
            String s = scanner.nextLine();
            groupChatClient.sendInfo(s);
        }

    }


}
