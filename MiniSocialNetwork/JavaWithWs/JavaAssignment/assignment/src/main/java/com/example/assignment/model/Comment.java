package com.example.assignment.model;

import com.example.assignment.entity.PostEntity;
import com.example.assignment.entity.UserEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Lob;

@Getter
@Setter
public class Comment {
    String text;
    Long post_id;

    public Comment() {
    }
}
