package com.example.assignment.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "friend")
@Getter
@Setter
public class FriendEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "friend_id")
    private Long id;
    @ManyToOne()
    @JoinColumn(name = "sender")
    private UserEntity Sender;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "accepter")
    private UserEntity Accepter;
    private Boolean Accepted = false;

    public FriendEntity() {
    }
}
