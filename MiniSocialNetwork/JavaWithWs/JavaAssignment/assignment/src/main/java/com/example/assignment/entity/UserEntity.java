package com.example.assignment.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "users")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;
    private String name;
    private String surname;
    private String login;
    private String pass;
    private String photo= "https://bootdey.com/img/Content/avatar/avatar7.png";
    private String visible = "all";

    public UserEntity(String name, String surname, String login, String pass) {
        this.name = name;
        this.surname = surname;
        this.login = login;
        this.pass = pass;
    }

    public UserEntity() {
    }
}
