package com.example.assignment.service;

import com.example.assignment.TokenHelper.TokenHelper;
import com.example.assignment.entity.FriendEntity;
import com.example.assignment.entity.PostEntity;
import com.example.assignment.entity.UserEntity;
import com.example.assignment.model.PostVisible;
import com.example.assignment.repository.FriendRepository;
import com.example.assignment.repository.PostRepository;
import com.example.assignment.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class PostService {
    final PostRepository postRepository;
    final UserRepository userRepository;
    final FriendRepository friendRepository;


    public List<PostEntity> getAllPosts(String token){
        List<PostEntity> allPosts = new ArrayList<>(2);
        List<PostEntity> postsVisibleAll = postRepository.getPostEntityByVisible("all");

        if(!token.equals("")){
            UserEntity user = userRepository.getUserEntityByLogin(TokenHelper.getLoginByToken(token));
            List<UserEntity> friendList = new ArrayList<>(2);
            List<FriendEntity> friends = friendRepository.getFriendUsers(user);
            List<PostEntity> postsVisibleFriend = postRepository.getPostEntityByVisible("friend");
            List<PostEntity> postsVisibleAuth = postRepository.getPostEntityByVisible("auth");

            for(FriendEntity friend:friends){
                if(friend.getAccepter().equals(user))
                    friendList.add(friend.getSender());
                else if(friend.getSender().equals(user))
                    friendList.add(friend.getAccepter());
            }
            for(PostEntity postFriend:postsVisibleFriend){
                if(friendList.contains(postFriend.getUser()))
                    allPosts.add(postFriend);
            }
            allPosts.addAll(postsVisibleAuth);
            allPosts.addAll(postsVisibleAll);
            return allPosts;
        }
        return postsVisibleAll;
    }

    public Boolean Post(PostEntity postEntity, String token){
        if(token != null) {
            UserEntity user = userRepository.getUserEntityByLogin(TokenHelper.getLoginByToken(token));
            if (postEntity.getText().length() > 10) {
                postEntity.setUser(user);
                postRepository.save(postEntity);
                return true;
            }
            return false;
        }
        return false;
    }

    public List<PostEntity> getPostByUser(UserEntity user){
        return postRepository.getPostEntitiesByUser(user);
    }
    public void ChangeVisibleOfPost(Long post_id, String visible){
        PostEntity post = postRepository.getPostEntityById(post_id);
        post.setVisible(visible);
        postRepository.save(post);
    }
}
