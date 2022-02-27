package com.example.assignment.controller;

import com.example.assignment.TokenHelper.TokenCookie;
import com.example.assignment.entity.CommentEntity;
import com.example.assignment.entity.PostEntity;
import com.example.assignment.model.Comment;
import com.example.assignment.model.PostVisible;
import com.example.assignment.service.CommentService;
import com.example.assignment.service.PostService;
import com.example.assignment.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@AllArgsConstructor
public class PostController {
    final PostService postService;
    final CommentService commentService;
    final UserService userService;

    @GetMapping("/")
    public String showPosts(Model model){
        String token = TokenCookie.getCookie().getValue();
        List<PostEntity> posts = postService.getAllPosts(token);
        Map<PostEntity,List<CommentEntity>> postWithComments = new HashMap<>(2);
        for (PostEntity post:posts) {
            List<CommentEntity> comments = commentService.getAllCommentsOfPost(post.getId());
            postWithComments.put(post,comments);
        }
        if(!token.equals("")) {
            model.addAttribute("userAuth", true);
            model.addAttribute("userInfo", userService.getUser(token));
            model.addAttribute("commentEntity",new Comment());
            model.addAttribute("posts", postWithComments);
            return "index";
        }
        return "redirect:/login";


    }


    @GetMapping("/post")
    public String showPostComment(@RequestParam Long post_id, Model model){
        String token = TokenCookie.getCookie().getValue();
        if(token.equals(""))
            return "login";

        List<CommentEntity> comments = commentService.getAllCommentsOfPost(post_id);
        model.addAttribute("comments",comments);
        model.addAttribute("new_comment", new CommentEntity());
        return "post";
    }

    @PostMapping("/post/visible/update")
    public String changeVisibleOfPost(Long post_id, String visible){
        String token = TokenCookie.getCookie().getValue();
        if(token != null){
            postService.ChangeVisibleOfPost(post_id,visible);
            return "redirect:/myProfile";
        }
        return "redirect:/login";
    }



    @PostMapping("/comment/save")
    public String savePostComment(Long post_id, String text, Model model){
        String token = TokenCookie.getCookie().getValue();
        if(!token.equals("")){
            commentService.commentSave(post_id,text,token);
            return "redirect:/";
        }
        return "redirect:/login";
    }



    @PostMapping("/addPost/save")
    public String post(PostEntity postEntity, Model model){
        Cookie cookie = TokenCookie.getCookie();
        String token = cookie.getValue();
        Boolean posted = postService.Post(postEntity, token);
        if(!posted){
            model.addAttribute("error", "Error in posting!!!");
            return "redirect:/myProfile";
        }
        return "redirect:/";
    }

}
