package com.example.assignment.repository;

import com.example.assignment.entity.PostEntity;
import com.example.assignment.entity.UserEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PostRepository extends CrudRepository<PostEntity,Long> {

    List<PostEntity> getPostEntityByVisible(String visible);
    PostEntity getPostEntityById(Long id);

    List<PostEntity> getPostEntitiesByUser(UserEntity user);

}
