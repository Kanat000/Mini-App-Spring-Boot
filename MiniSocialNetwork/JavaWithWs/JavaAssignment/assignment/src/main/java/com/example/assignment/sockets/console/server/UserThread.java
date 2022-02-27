package com.example.assignment.sockets.console.server;

import lombok.Getter;
import lombok.Setter;
import project.friendmeapp.springboot.models.Post;
import project.friendmeapp.springboot.models.User;
import project.friendmeapp.springboot.sockets.console.posts.PostServiceImpl;
import project.friendmeapp.springboot.sockets.console.posts.UserServiceImpl;

import java.io.*;
import java.net.Socket;

public class UserThread extends Thread {
    private final Socket socket;
    private final Server server;
    private PrintWriter writer;
    private BufferedReader reader;
    @Getter
    private String clientName;
    @Getter
    private String clientPassword;
    @Getter @Setter
    private PostServiceImpl postServiceImpl = new PostServiceImpl();
    private UserServiceImpl userServiceImpl = new UserServiceImpl();
    @Getter @Setter
    private User user = new User();
    private Boolean userValid;
    @Getter @Setter
    private Long countOfUserPosts = 0L;
    @Getter @Setter
    private Post post = new Post();

    UserThread(Socket socket, Server server) {
        this.socket = socket;
        this.server = server;
    }

    @Override
    public void run() {
        try {
            InputStream input = socket.getInputStream();
            reader = new BufferedReader(new InputStreamReader(input));

            OutputStream output = socket.getOutputStream();

            writer = new PrintWriter(output, true);
            writer.println("Hello, Guest! Welcome to our server! " +
                    "In order to use features of the server please login in format => username:password");

            String clientData;

            while (true) {
                clientData = reader.readLine();

                if(clientData.contains(":")){
                    this.clientName = clientData.split(":")[0];
                    this.clientPassword = clientData.split(":")[1];
                }else
                    writer.println("Format is not corresponds to username:password!");

                userValid = userServiceImpl.checkUser(clientName, clientPassword);

                if(userValid){
                    if(server.getUserThreadMap().containsKey(clientName)){
                        writer.println("This login already in use!");
                    }else{
                        this.user = userServiceImpl.getUserByUsername(clientName);
                        server.addUserThread(clientName, this);
                        if(postServiceImpl.userHasUpdatedPosts(user.getId())){
                            this.countOfUserPosts = postServiceImpl.getUpdatedPost(user.getId());
                        }else
                            this.countOfUserPosts = 0L;

                        break;
                    }
                }else
                    writer.println("Login and password incorrect! Try again!");
            }

            String msg;
            do {
                writer.println("Server has 3 options allowed for users:\n" +
                        "1. Create post     " +
                        "2. Refresh page     " +
                        "3. Exit\n" +
                        "Please, choose the option you wish by digit(1,2,3): ");
                msg = reader.readLine();

                if(msg.equals("1")){
                    writer.println("|Post creation page|\n" +
                            "Post has 3 parameters which are MANDATORY to enter:\n" +
                            "1. content     " +
                            "2. visibility     " +
                            "3. enabling or disabling comments \n" +
                            "Enter your text: ");

                    String content = reader.readLine();

                    writer.println("Select visibility option(all, authorized, friends):");

                    String visibility;

                    while(true){
                        visibility = reader.readLine();
                        if(visibility.equals("all") || visibility.equals("friends") || visibility.equals("authorized")){
                            break;
                        }else
                            writer.println("Please carefully input visibility options [all, authorized, friends]");
                    }

                    writer.println("Select comment permission(true or false):");

                    String commentPermission;

                    while(true){
                        commentPermission = reader.readLine();
                        if(commentPermission.equals("false") || commentPermission.equals("true")){
                            break;
                        }else
                            writer.println("Please carefully input comment permission options [true or false]");
                    }

                    this.post = postServiceImpl.savePost(content, visibility, commentPermission, user);

                    server.addPost(post);
                    if(countOfUserPosts == 0){
                        postServiceImpl.savePostForUser(user.getId(), post.getId());
                    }

                    writer.println("Post was successfully created!");

                }else if(msg.equals("2")){
                    server.refreshPage(clientName,false);
                }else {
                    break;
                }

            } while (true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String message) {
        writer.println(message);
    }

}
