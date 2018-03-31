package com.lyb.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 *
 * a client for test talk server.
 *
 * @author liyubo
 * @create 2018-03-31 下午2:04
 **/
public class ClientRobot {

    private static final Logger logger = LoggerFactory.getLogger(ClientRobot.class);

    private static final Integer PORT = 13000;

    public static void main(String [] args){
        Socket socket = new Socket();
        try {
            socket.connect(new InetSocketAddress("127.0.0.1",PORT));
            InputStream inputStream = socket.getInputStream();
            byte[] bytes = new byte[1024];
            int length;
            while( (length=inputStream.read(bytes)) != -1){
                logger.info("get from server = "+new String(bytes,0,length));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
