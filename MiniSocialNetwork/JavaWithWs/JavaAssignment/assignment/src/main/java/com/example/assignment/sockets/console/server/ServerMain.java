package com.example.assignment.sockets.console.server;

import java.io.IOException;

public class ServerMain {
    public static void main(String[] args) throws IOException {

        int port = 5000;

        Server server = new Server(port);
        server.exec();
    }
}
