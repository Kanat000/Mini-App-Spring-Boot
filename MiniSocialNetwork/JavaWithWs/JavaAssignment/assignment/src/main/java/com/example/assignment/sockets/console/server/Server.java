package com.example.assignment.sockets.console.server;

import lombok.Getter;
import project.friendmeapp.springboot.models.Post;
import project.friendmeapp.springboot.sockets.console.posts.PostServiceImpl;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class Server {
    @Getter
    //map with username and user's threads parameters
    private final Map<String, UserThread> userThreadMap=new HashMap<>();
    PostServiceImpl postServiceImpl = new PostServiceImpl();
    @Getter
    private int port;

    public Server(){}

    public Server(int port) {
        this.port=port;
    }

    public UserThread getUserThread(String username) {
        return userThreadMap.get(username);
    }

    public Long numberOfPostsFromDB(){
        return postServiceImpl.getPostCount();
    }

    public void addUserThread(String username, UserThread userThread) {
        userThreadMap.put(username, userThread);
        System.out.println("User "+ username +" added to server");
    }

    public void addPost(Post post){
        System.out.println("New post was added to server");
    }

    public void exec() throws IOException {
        ServerSocket serverSocket= new ServerSocket(this.port);
        // everytime calls checkAndSendEach10MinutesIfNewPostExist method
        checkAndSendEach10MinutesIfNewPostExist();

        System.out.println("Waiting for clients...");

        while (true){
            // every time waiting new clients to proceed command after login
            Socket socket=serverSocket.accept();
            UserThread userThread=new UserThread(socket, this);
            userThread.start();
        }
    }

    private void checkAndSendEach10MinutesIfNewPostExist() {
        // using TimerTask we call refreshPage method every 10minutes
        // then check new post there
        // after check if there are new posts we send it to each client
        TimerTask repeatedTask = new TimerTask() {
            @Override
            public void run() {
                if(!getUserThreadMap().isEmpty()){
                    for(UserThread userThread: getUserThreadMap().values()){
                        refreshPage(userThread.getClientName(), true);
                        System.out.println(userThread.getClientName());
                    }
                }
            }
        };

        Timer timer = new Timer("Check for update new post and send it to users...");

        // time interval 600*1000ml we got 10 minutes
        long period = 1000L * 600L;


        timer.scheduleAtFixedRate(repeatedTask,1000,period);
    }

    public void refreshPage(String username, boolean isItTimer){
        // using username we get user thread where consist all information
        UserThread thread = getUserThread(username);
        // get id of user from thead
        Long userId = thread.getUser().getId();
        // get updated post id
        Long userPostLastId = postServiceImpl.getUpdatedPost(userId);

        // if user post's id in table check_in less than number of posts in table post
        if(userPostLastId < numberOfPostsFromDB()){
            // then check is it timer called or user chose option 2(refresh page)
            if(isItTimer){
                thread.sendMessage("|Each 10 minutes server checks new posts and sends them if they exist|");
            }else
                thread.sendMessage("Page refreshed successfully! New Posts available, check it now:");

            // get all posts starting from post id in check_in table till number of posts in posts table
            for(Post post: postServiceImpl.getAllPostsFromUserPostId(userPostLastId)){
                getUserThread(username).sendMessage("=======================================" +
                        "\nPost Id: " + post.getId()+
                        "\nContent: "+ post.getContent()+
                        "\nVisibility: "+ post.getVisibility()+
                        "\nComment allowed: "+post.getCommentAllowed()+
                        "\n=======================================");
            }
            // if user already has updated post list in check_in table
            // then we update row to count of post in table posts
            if(postServiceImpl.userHasUpdatedPosts(userId)){
                postServiceImpl.updateNewPostList(userId, numberOfPostsFromDB());
            }else {
                // if user doesn't have any data in check_in table we should create user_id and post_id
                postServiceImpl.savePostForUser(userId, numberOfPostsFromDB());
            }
        }else if(!isItTimer){
            // if user chose refresh page and there are no new posts then this text appears
            thread.sendMessage("=======================================\n" +
                    "There is no new posts on server." +
                    "\n=======================================");
        }
    }
}
