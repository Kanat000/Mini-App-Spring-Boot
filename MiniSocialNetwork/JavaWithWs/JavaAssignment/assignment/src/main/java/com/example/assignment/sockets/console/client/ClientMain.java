package com.example.assignment.sockets.console.client;

public class ClientMain {
    public static void main(String[] args) {

        // port for server
        int port = 5000;

        // create client
        Client client = new Client("localhost", port);
        // calling method execute
        client.execute();
    }
}
