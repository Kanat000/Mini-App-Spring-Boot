package com.example.assignment.repository;

import com.example.assignment.entity.FriendEntity;
import com.example.assignment.entity.UserEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface FriendRepository extends CrudRepository<FriendEntity, Long> {
   @Query("Select f.Accepted from FriendEntity f where f.Sender = ?1 and f.Accepter = ?2")
   Boolean checkAccepted(UserEntity sender, UserEntity accepter);

   @Query("Select count(f) from FriendEntity f where f.Sender = ?1 and f.Accepter = ?2")
   Long checkSent(UserEntity sender, UserEntity accepter);

   @Query("Select f from FriendEntity f where f.Accepter = ?1 and f.Accepted = false")
   List<FriendEntity> getNotification(UserEntity accepter);

   @Query("Select f from FriendEntity f where (f.Accepter = ?1 or f.Sender = ?1) and f.Accepted=true")
   List<FriendEntity> getFriendUsers(UserEntity user);

   @Query("Select f from FriendEntity f where f.Sender = ?1 and f.Accepter = ?2")
   FriendEntity getFriendByUsers(UserEntity sender,UserEntity accepter);

}
