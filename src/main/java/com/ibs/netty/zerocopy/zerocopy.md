#3.14 NIO 与零拷贝
##3.14.1 零拷贝基本介绍
零拷贝是网络编程的关键，很多性能优化都离不开。
在 Java 程序中，常用的零拷贝有 mmap（内存映射）和 sendFile。那么，他们在 OS 里，到底是怎么样的一个的设计？我们分析 mmap 和 sendFile 这两个零拷贝
另外我们看下 NIO 中如何使用零拷贝


##3.14.2 传统 IO 数据读写
Java 传统 IO 和网络编程的一段代码

File file = new File("test.txt");
RandomAccessFile raf = new RandomAccessFile(file, "rw");

byte[] arr = new byte[(int) file.length()];
raf.read(arr);

Socket socket = new ServerSocket(8080).accept();
socket.getOutputStream().write(arr);
Copy to clipboardErrorCopied
##3.14.3 传统 IO 模型
![Image text](https://dongzl.github.io/netty-handbook/_media/chapter03/chapter03_17.png)


DMA：direct memory access 直接内存拷贝（不使用 CPU）

##3.14.4 mmap 优化
mmap 通过内存映射，将文件映射到内核缓冲区，同时，用户空间可以共享内核空间的数据。这样，在进行网络传输时，就可以减少内核空间到用户空间的拷贝次数。如下图
mmap 示意图
![Image text](https://dongzl.github.io/netty-handbook/_media/chapter03/chapter03_18.png)

##3.14.5 sendFile 优化
Linux2.1 版本提供了 sendFile 函数，其基本原理如下：数据根本不经过用户态，直接从内核缓冲区进入到 SocketBuffer，同时，由于和用户态完全无关，就减少了一次上下文切换
示意图和小结
![Image text](https://dongzl.github.io/netty-handbook/_media/chapter03/chapter03_19.png)


提示：零拷贝从操作系统角度，是没有 cpu 拷贝
Linux在2.4 版本中，做了一些修改，避免了从内核缓冲区拷贝到 Socketbuffer 的操作，直接拷贝到协议栈，从而再一次减少了数据拷贝。具体如下图和小结：
![Image text](https://dongzl.github.io/netty-handbook/_media/chapter03/chapter03_20.png)


这里其实有一次 cpu 拷贝 kernel buffer -> socket buffer 但是，拷贝的信息很少，比如 lenght、offset 消耗低，可以忽略
##3.14.6 零拷贝的再次理解
我们说零拷贝，是从操作系统的角度来说的。因为内核缓冲区之间，没有数据是重复的（只有 kernel buffer 有一份数据）。
零拷贝不仅仅带来更少的数据复制，还能带来其他的性能优势，例如更少的上下文切换，更少的 CPU 缓存伪共享以及无 CPU 校验和计算。
##3.14.7 mmap 和 sendFile 的区别
mmap 适合小数据量读写，sendFile 适合大文件传输。
mmap 需要 4 次上下文切换，3 次数据拷贝；sendFile 需要 3 次上下文切换，最少 2 次数据拷贝。
sendFile 可以利用 DMA 方式，减少 CPU 拷贝，mmap 则不能（必须从内核拷贝到 Socket缓冲区）。