package com.example.assignment.repository;

import com.example.assignment.entity.CommentEntity;
import com.example.assignment.entity.PostEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository()
public interface CommentRepository extends CrudRepository<CommentEntity,Long> {
    List<CommentEntity> getCommentEntitiesByPost(PostEntity post);
}
