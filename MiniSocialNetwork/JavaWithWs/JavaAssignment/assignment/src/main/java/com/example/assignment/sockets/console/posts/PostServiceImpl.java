package com.example.assignment.sockets.console.posts;


import com.example.assignment.entity.PostEntity;
import com.example.assignment.entity.UserEntity;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PostServiceImpl {

    private Long last_id = 0L;
    private Long numberOfPosts = 0L;

    public PostEntity savePost(String content, String visibility, String commentPermission, UserEntity user) {

        // savind post using insert into query in prepared statement
        try {
            PreparedStatement preparedStatement =
                    JdbcConnection.getConnection().prepareStatement
                            ("INSERT INTO `posts` (`id`, `text`, `visibile`, `user_id`)" +
                                    " VALUES (?, ?, ?, ?) ", Statement.RETURN_GENERATED_KEYS);

            preparedStatement.setNull(1, Types.BIGINT);
            preparedStatement.setString(2, content);
            preparedStatement.setString(3, visibility);
            preparedStatement.setLong(4, user.getId());
            preparedStatement.executeUpdate();
            ResultSet rs = preparedStatement.getGeneratedKeys();
            if(rs.next()){
                // get last id
                last_id = rs.getLong(1);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        // putting data to Post object and returning it
        PostEntity post = new PostEntity();

        post.setId(last_id);
        post.setUser(user);
        post.setText(content);
        post.setVisible(visibility);

        return post;
    }

    public void savePostForUser(Long userId, Long postId) {
        // in order to get posts which user already seen
        // we should save it everytime to database in table check_in
        try {
            PreparedStatement preparedStatement =
                    JdbcConnection.getConnection().prepareStatement
                            ("INSERT INTO `check_in` (`user_id`, `saved_last_post_id`)" +
                                    " VALUES (?, ?) ");

            preparedStatement.setLong(1,userId);
            preparedStatement.setLong(2,postId);

            preparedStatement.executeUpdate();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public Long getPostCount() {
        // query to find count of posts
        try {
            Statement statement = JdbcConnection.getConnection().createStatement();
            String query = "SELECT COUNT(*) as row_count FROM posts";
            ResultSet resultSet = statement.executeQuery(query);

            resultSet.next();

            numberOfPosts = resultSet.getLong("row_count");

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return numberOfPosts;
    }

    public List<PostEntity> getAllPostsFromUserPostId(Long id){
        // query to get all posts from id of post
        List<PostEntity> posts = new ArrayList<>();

        try {
            Statement statement = JdbcConnection.getConnection().createStatement();
            String query = "SELECT id, content,visibility,comment_allowed, user_id FROM posts WHERE id > "+id;
            ResultSet resultSet = statement.executeQuery(query);
            UserServiceImpl userService = new UserServiceImpl();

            while (resultSet.next()){
                PostEntity post = new PostEntity();

                post.setId(resultSet.getLong("id"));
                post.setText(resultSet.getString("content"));
                post.setVisible(resultSet.getString("visibility"));
                post.setUser(userService.getUserById(resultSet.getLong("user_id")));
                posts.add(post);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return posts;
    }

    public Long getUpdatedPost(Long userId) {
        // we saved posts to check_in table in database and should be able to get them
        // query to return these post's id using user_id
        long lastSavedPost_id = 0L;
        try {
            PreparedStatement preparedStatement =
                    JdbcConnection.getConnection().prepareStatement
                            ("SELECT saved_last_post_id from check_in where user_id = ?");

            preparedStatement.setLong(1,userId);
            ResultSet rs = preparedStatement.executeQuery();

            while(rs.next()){
                lastSavedPost_id = rs.getLong("saved_last_post_id");
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return lastSavedPost_id;
    }

    public Boolean userHasUpdatedPosts(Long userId){
        // checking query for is post already updated for user using user_id

        Long postId = null;

        try {
            PreparedStatement preparedStatement =
                    JdbcConnection.getConnection().prepareStatement
                            ("SELECT saved_last_post_id from check_in where user_id = ?");

            preparedStatement.setLong(1,userId);
            ResultSet rs = preparedStatement.executeQuery();

            while(rs.next()){
                postId = rs.getLong("saved_last_post_id");
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return postId != null;
    }

    public void updateNewPostList(Long userId, Long postId) {
        // query to update post id and user id in table check_in

        try {
            PreparedStatement preparedStatement =
                    JdbcConnection.getConnection().prepareStatement
                            ("UPDATE `check_in` SET `saved_last_post_id` = ? WHERE `check_in`.`user_id` = ?");

            preparedStatement.setLong(1,postId);
            preparedStatement.setLong(2,userId);

            preparedStatement.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
