package com.example.assignment.repository;

import com.example.assignment.entity.UserEntity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface UserRepository extends CrudRepository<UserEntity, Long> {
    Boolean existsUserEntityByLogin(String login);
    UserEntity getUserEntityByLogin(String login);
    @Query("Select u from UserEntity u where u.login <> ?1")
    List<UserEntity> getAllUsers(String login);


}
