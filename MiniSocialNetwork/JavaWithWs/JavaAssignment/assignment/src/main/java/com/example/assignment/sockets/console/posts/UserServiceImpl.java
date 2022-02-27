package com.example.assignment.sockets.console.posts;
import com.example.assignment.entity.UserEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import project.friendmeapp.springboot.models.User;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserServiceImpl {

    public Boolean checkUser(String clientName, String clientPassword) {
        // this method checks user's username and password from database

        UserEntity user = new UserEntity();

        try {
            PreparedStatement preparedStatement = JdbcConnection.getConnection().prepareStatement
                    ("SELECT username, password FROM users WHERE username = ?");

            preparedStatement.setString(1, clientName);

            ResultSet resultSet = preparedStatement.executeQuery();


            while(resultSet.next()){
                user.setPass(resultSet.getString("password"));
                user.setLogin(resultSet.getString("username"));
            }
            if(user == null){
                return false;
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }


        return passwordEncoder.matches(clientPassword, user.getPassword());
    }

    public User getUserByUsername(String clientName) {
        //this method get user by usernmae using prepared statement

        User user =  new User();

        try {
            PreparedStatement preparedStatement = JdbcConnection.getConnection().prepareStatement
                    ("SELECT id, username, email, password, restrictions FROM users WHERE username = ?");

            preparedStatement.setString(1, clientName);

            ResultSet resultSet = preparedStatement.executeQuery();

            while(resultSet.next()){
                user.setId(resultSet.getLong("id"));
                user.setUsername(resultSet.getString("username"));
                user.setEmail(resultSet.getString("email"));
                user.setPassword(resultSet.getString("password"));
                user.setRestrictions(resultSet.getString("restrictions"));
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return user;
    }

    public User getUserById(Long userId) {
        // get user by id using prepared statement
        User user =  new User();

        try {
            PreparedStatement preparedStatement = JdbcConnection.getConnection().prepareStatement
                    ("SELECT id, username, email, password, restrictions FROM users WHERE id = ?");

            preparedStatement.setLong(1, userId);

            ResultSet resultSet = preparedStatement.executeQuery();

            while(resultSet.next()){
                user.setId(resultSet.getLong("id"));
                user.setUsername(resultSet.getString("username"));
                user.setEmail(resultSet.getString("email"));
                user.setPassword(resultSet.getString("password"));
                user.setRestrictions(resultSet.getString("restrictions"));
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return user;
    }

}
