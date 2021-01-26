package com.ibs.netty.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
/**
 * 对右图的说明:
 * 1.当客户端连接时，会通过ServerSocketChannel得到SocketChannel
 * 2.selector开始监听
 * 3.将socketChannel注册到Selector上，register(Selector sel, int ops),
 * 一个selector.上可以注册多个SocketChannel
 * 4.注册后返回一个SelectionKey,会和该Selector关联(集合)
 * 5.Selector进行监听select方法，返回有事件发生的通道的个数.
 * 6.进一步得到各个SelectionKey (有事件发生)
 * 7.在通过SelectionKey反向获取SocketChannel，方法chanel()
 * 8.可以通过 得到的channel，完成业务处理
 *
 * */
public class NIOServer {
    public static void main(String[] args) throws IOException {
        // 创建server socket channel：在服务器端监听新的客户端socket连接
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        //绑定一个端口6666，在服务器端监听
        serverSocketChannel.socket().bind(new InetSocketAddress(6666));
        // 设置为非阻塞
        serverSocketChannel.configureBlocking(false);
        //得到一个selector对象
        Selector selector = Selector.open();
        // 把serverSocketChannel注册到selector,关心事件为OP_ACCEPT 连接事件
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        // 循环等待客户端连接
        while (true){
            // 等待1秒，如果没有事件发生，返回
            if (selector.select(1000) == 0){
                //没有事件发生
                System.out.println("服务器等待了1s，无连接");
                continue;
            }

            //如果返回的不是0
            // 1.如果返回>0，表示已经获取道关注的事件
            // 2.selector.selectedKeys(),返回关注事件的集合
            //  通过selectionkeys反向获取通道
            Set<SelectionKey> selectionKeySet = selector.selectedKeys();
            // 遍历selectionKeySet
            Iterator<SelectionKey> keyIterator = selectionKeySet.iterator();
            while (keyIterator.hasNext()){
                // 获取到selectionkey
                SelectionKey key = keyIterator.next();
                //根据key对应的通道发生的事件做相应处理
                if (key.isAcceptable()){
                    // 如果是OP_ACCEPT,有新的客户端连接
                    // 给该客户端生成一个socketchannel
                    SocketChannel socketChannel = serverSocketChannel.accept();
                    // 将socketchannel注册到selector,关注事件为读，同时给channel关联一个buffer
                    socketChannel.register(selector,SelectionKey.OP_READ, ByteBuffer.allocate(1024));
                    System.out.println("客户端连接成功，生成了一个socketchannel" + socketChannel.hashCode());



                }
                // 读取
                if (key.isReadable()){
                    //通过key反向获取到对应的channel
                    SocketChannel channel = (SocketChannel) key.channel();
                    // 获取到该channel关联的buffer
                    ByteBuffer buffer = (ByteBuffer) key.attachment();
                    // 把当前channel数据读入到buffer里面去
                    channel.read(buffer);
                    System.out.println("客户端发送数据" + new String(buffer.array()));

                }
                // 手动从集合中移除当前的selectionKey,防止重复操作
                keyIterator.remove();

            }
        }

    }
}
