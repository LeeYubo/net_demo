package com.lyb.bio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * blocking io server.
 *
 * @author liyubo
 * @create 2018-03-31 下午1:34
 **/
public class BlockingIOServer {

    private static final Logger logger = LoggerFactory.getLogger(BlockingIOServer.class);

    private static final Integer PORT = 13000;

    public static void main(String [] args){
        BlockingIOServer blockingIOServer = new BlockingIOServer();
        blockingIOServer.talkServer();
    }

    public void talkServer(){
        try {
            final ServerSocket serverSocket = new ServerSocket();
            serverSocket.bind(new InetSocketAddress(PORT));
            logger.info("talk server start success.");
            while (true){
                logger.info("I am waiting to accept a new socket.......");
                Socket socket = serverSocket.accept();
                logger.info("i got a new socket, socket address is "+socket.toString());
                SocketHandler socketHandler = new SocketHandler(socket);
                Thread thread = new Thread(socketHandler);
                logger.info("i will start a new thread to handle your socket, Thread name = "+thread.getName());
                thread.start();
                logger.info("handled finished, and i will have e rest for 1 second.");
                Thread.sleep(10000);
                logger.info("i wake up....");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        logger.info("this is a blocking io test server.");
    }


    /**
     * thread to handle client session.
     */
    private class SocketHandler implements Runnable {

        private Socket socket;
        private OutputStream outputStream;

        SocketHandler(Socket socket){
            this.socket = socket;
        }

        public void run() {
            try {
                outputStream = socket.getOutputStream();
                outputStream.write("Hi, i am the Talk Server, how are you.".getBytes());
                outputStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (null!=outputStream){
                    try {
                        outputStream.close();
                    } catch (IOException e) {
                        logger.error("close OutputStream has an error."+e.getMessage());
                    }
                }
            }
        }
    }
}
