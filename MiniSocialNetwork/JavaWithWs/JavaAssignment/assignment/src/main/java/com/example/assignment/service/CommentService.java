package com.example.assignment.service;

import com.example.assignment.TokenHelper.TokenHelper;
import com.example.assignment.entity.CommentEntity;
import com.example.assignment.entity.PostEntity;
import com.example.assignment.model.Comment;
import com.example.assignment.repository.CommentRepository;
import com.example.assignment.repository.PostRepository;
import com.example.assignment.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class CommentService {

    final CommentRepository commentRepository;
    final PostRepository postRepository;
    final UserRepository userRepository;

    public List<CommentEntity> getAllCommentsOfPost(Long post_id) {
        PostEntity post = postRepository.getPostEntityById(post_id);
        return commentRepository.getCommentEntitiesByPost(post);
    }

    public void commentSave(Long post_id, String text, String token) {

            CommentEntity comment = new CommentEntity();
            comment.setPost(postRepository.getPostEntityById(post_id));
            comment.setUser(userRepository.getUserEntityByLogin(TokenHelper.getLoginByToken(token)));
            comment.setText(text);
            commentRepository.save(comment);



    }
}