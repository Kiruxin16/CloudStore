package com.cloud;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class cloudServer {


    private static Socket socket;

    public static void main(String[] args) throws IOException {
        try(ServerSocket server = new ServerSocket(8189)){
            while (true){
                socket =server.accept();
                servHandler handler = new servHandler(socket);
                new Thread(handler).start();
                System.out.println("Client connected!");
            }
        }
    }
}
