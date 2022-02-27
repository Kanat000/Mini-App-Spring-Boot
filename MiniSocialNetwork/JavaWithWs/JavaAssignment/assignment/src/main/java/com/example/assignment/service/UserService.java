package com.example.assignment.service;

import com.example.assignment.TokenHelper.TokenHelper;
import com.example.assignment.entity.FriendEntity;
import com.example.assignment.entity.PostEntity;
import com.example.assignment.entity.UserEntity;
import com.example.assignment.model.Login;
import com.example.assignment.repository.FriendRepository;
import com.example.assignment.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class UserService {
    final UserRepository userRepository;
    final FriendRepository friendRepository;
    public String register(UserEntity userEntity) {
        if (userEntity.getName().length() < 3)
            return "nameError";
        else if (userEntity.getSurname().length() < 3)
            return "surnameError";
        else if (userEntity.getLogin().length() < 4)
            return "loginError";
        else if (userRepository.existsUserEntityByLogin(userEntity.getLogin()))
            return "loginHas";
        else if (userEntity.getPass().length() < 5)
            return "passError";
        else{
            userRepository.save(userEntity);
            return "allCorrect";
        }
    }

    public String singInUser(Login logIn, String token) {
        if (token.equals("")) {
            boolean check = userRepository.existsUserEntityByLogin(logIn.getLogin())
                    && userRepository.getUserEntityByLogin(logIn.getLogin()).getPass().equals(logIn.getPass());
            if (check) {
                String tkn;
                if (TokenHelper.blackListContains(logIn.getLogin())) {
                    tkn = TokenHelper.getFromBlackList(logIn.getLogin());
                } else {
                    tkn = TokenHelper.getToken(logIn.getLogin());
                }

                return tkn;
            }

            return "Incorrect";
        }
        return "Error";
    }

    public Boolean LogOut(String token) {
        if (token != null) {
            String login = TokenHelper.getLoginByToken(token);
            TokenHelper.addToBlackList(login, token);
            return true;
        }
        return false;
    }

    public List<UserEntity> getUsers(String token){
        return userRepository.getAllUsers(TokenHelper.getLoginByToken(token));
    }
    public List<UserEntity> getFriends(String token){
        UserEntity user = userRepository.getUserEntityByLogin(TokenHelper.getLoginByToken(token));
        List<FriendEntity> friends = friendRepository.getFriendUsers(user);
        List<UserEntity> allFriendUsers = new ArrayList<>(2);
        for(FriendEntity friend: friends){
            if(friend.getAccepter().equals(user) && !allFriendUsers.contains(friend.getSender()))
                allFriendUsers.add(friend.getSender());
            else{
                if(!allFriendUsers.contains(friend.getAccepter()))
                    allFriendUsers.add(friend.getAccepter());
            }
        }
        return allFriendUsers;
    }

    public boolean sendRequestFriend(String token, String login){
        if(!token.equals("")){
        String senderLogin = TokenHelper.getLoginByToken(token);
        UserEntity sender = userRepository.getUserEntityByLogin(senderLogin);
        UserEntity accepter = userRepository.getUserEntityByLogin(login);
        FriendEntity friendEntity = new FriendEntity();
        friendEntity.setSender(sender);
        friendEntity.setAccepter(accepter);
        friendRepository.save(friendEntity);
        return true;
        }
        return false;
    }
    public List<FriendEntity> getNotification(String token){

            UserEntity user = userRepository.getUserEntityByLogin(TokenHelper.getLoginByToken(token));
            return friendRepository.getNotification(user);


    }
    public void acceptRequestFriend(String token, String login){
        UserEntity accepter = userRepository.getUserEntityByLogin(TokenHelper.getLoginByToken(token));
        UserEntity sender = userRepository.getUserEntityByLogin(login);
        FriendEntity friend = friendRepository.getFriendByUsers(sender, accepter);
        friend.setAccepted(true);
        friendRepository.save(friend);
    }

    public UserEntity getUserProfile(String login, String token){
        UserEntity user = userRepository.getUserEntityByLogin(login);
        if(token.equals("")){
            if(user.getVisible().equals("auth") || user.getVisible().equals("friend")){
               return null;
            }
            return user;
        }else {
            UserEntity me = userRepository.getUserEntityByLogin(TokenHelper.getLoginByToken(token));
            Boolean check1 = friendRepository.checkAccepted(me, user);
            Boolean check2 = friendRepository.checkAccepted(user, me);
                if (user.getVisible().equals("friend")) {
                    if((check1!=null && check1) || (check2!=null && check2)) {
                        return user;
                    }
                    return null;
                }
                return user;
        }
    }

    public UserEntity getUser(String token){
        return userRepository.getUserEntityByLogin(TokenHelper.getLoginByToken(token));
    }

    public void changeVisible(String token, String visible){
        UserEntity user = userRepository.getUserEntityByLogin(TokenHelper.getLoginByToken(token));
        user.setVisible(visible);
        userRepository.save(user);
    }

    public Boolean checkOfSentRequest(String token, UserEntity user){
        Long table_size = friendRepository.checkSent(userRepository.getUserEntityByLogin(TokenHelper.getLoginByToken(token)), user);
        return table_size > 0;
    }
}