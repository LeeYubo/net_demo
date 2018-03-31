package com.lyb.nio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * no blocking IO talk server.
 *
 * @author liyubo
 * @create 2018-03-31 下午1:35
 **/
public class NoBlockingIOServer {

    private static final Logger logger = LoggerFactory.getLogger(NoBlockingIOServer.class);

    private Integer PORT = 13000;
    private ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

    public static void main(String [] args){
        NoBlockingIOServer noBlockingIOServer = new NoBlockingIOServer();
        noBlockingIOServer.talkServer();
    }

    public void talkServer(){
        try {
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);
            ServerSocket serverSocket = serverSocketChannel.socket();
            serverSocket.bind(new InetSocketAddress(PORT));
            Selector selector = Selector.open();
            serverSocketChannel.register(selector,SelectionKey.OP_ACCEPT);
            logger.info("register server channel to selector.");
            while(true){
                logger.info("start to select.");
                int numbers = selector.select();
                logger.info("numbers = "+numbers);

                Set<SelectionKey> selectionKeySet = selector.selectedKeys();
                if(CollectionUtils.isEmpty(selectionKeySet)){
                    logger.info("there was no client connect now, i will have a rest first for 1 second.");
                    Thread.sleep(1000);
                }
                Iterator<SelectionKey> selectionKeyIterator = selectionKeySet.iterator();
                while (selectionKeyIterator.hasNext()){
                    SelectionKey selectionKey = selectionKeyIterator.next();
                    selectionKeyIterator.remove();
                    // accept event.
                    if(selectionKey.isAcceptable()){
                        ServerSocketChannel serverSocketChannel1 = (ServerSocketChannel) selectionKey.channel();
                        SocketChannel socketChannel = serverSocketChannel1.accept();
                        registerChannel(socketChannel,selector);
                        writeData(socketChannel);
                    }

                    // read event.
                    if(selectionKey.isReadable()){
                        SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
                        readDataFromChannel(socketChannel);
                    }

                    // write event
                    if(selectionKey.isWritable()){
                        logger.info("got a writable event.");
                        SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
                        byteBuffer.clear();
                        byteBuffer.put("what's wrong Erin.".getBytes());
                        byteBuffer.flip();
                        socketChannel.write(byteBuffer);
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    private void registerChannel(SocketChannel socketChannel, Selector selector){
        try {
            socketChannel.configureBlocking(false);
            socketChannel.register(selector,SelectionKey.OP_READ | SelectionKey.OP_WRITE);
        } catch (IOException e) {
            logger.error("accept a new socket and create channel failed.");
        }
        logger.info("got a connection from client {}.",socketChannel);
    }

    private void writeData(SocketChannel socketChannel) throws IOException {
        byteBuffer.clear();
        byteBuffer.put("Hi Erin, I am PangBo Lee.".getBytes());
        byteBuffer.flip();
        socketChannel.write(byteBuffer);
    }


    private void readDataFromChannel(SocketChannel socketChannel) throws IOException {
        byteBuffer.clear();
        int readCount;
        while((readCount=socketChannel.read(byteBuffer))>0){
            byte [] bytes = new byte[readCount];
            logger.info("i got message from client, message = {}.",byteBuffer.get(bytes));
        }
    }
}
