package com.example.assignment.sockets.console.client;

import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {
    private String hostname;
    private int port;

    @Getter @Setter
    private String username;

    @Getter @Setter
    private String password;

    public Client(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
    }

    public void execute() {
        try {
            // when we run program in ClientMain
            // it calls method "execute" where we specify our location or hostname and port number
            // to connect to server
            Socket socket = new Socket(hostname, port);

            System.out.println("Connected to the server");

            // gets each message from server side
            new ReadThread(socket, this).start();
            // inputs data while option is not Exit
            new WriteThread(socket, this).start();

        } catch (UnknownHostException ex) {
            System.out.println("Server not found: " + ex.getMessage());
        } catch (IOException ex) {
            System.out.println("I/O Error: " + ex.getMessage());
        }

    }

}
