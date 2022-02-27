package com.example.assignment.controller;

import com.example.assignment.TokenHelper.TokenCookie;
import com.example.assignment.entity.CommentEntity;
import com.example.assignment.entity.FriendEntity;
import com.example.assignment.entity.PostEntity;
import com.example.assignment.entity.UserEntity;
import com.example.assignment.model.Login;
import com.example.assignment.model.PostVisible;
import com.example.assignment.service.CommentService;
import com.example.assignment.service.PostService;
import com.example.assignment.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@AllArgsConstructor
public class UserController {
    final UserService userService;
    final PostService postService;
    final CommentService commentService;

    @GetMapping("/register")
    public String register(Model model){
        model.addAttribute("user", new UserEntity());
        return "register";
    }
    @GetMapping("/login")
    public String login(Model model){
        model.addAttribute("login", new Login());
        return "login";
    }

    @PostMapping("/register/save")
    public String registerCheck(UserEntity user, Model model){
        String response = userService.register(user);
        if(response.equals("nameError")) {
            model.addAttribute("error", "Name is short!!!");
            return "register";
        }
        else if(response.equals("surnameError")) {
            model.addAttribute("error", "Surname is short!!!");
            return "register";
        }else if(response.equals("loginError")) {
            model.addAttribute("error", "Login is short!!!");
            return "register";
        }else if(response.equals("loginHas")) {
            model.addAttribute("error", "Login already exists!!!");
            return "register";
        }else if(response.equals("passError")) {
            model.addAttribute("error", "Password is short!!!");
            return "register";
        }else{
            model.addAttribute("login", new Login(user.getLogin(),user.getPass()));
            return "login";
        }
    }

    @PostMapping("/login/check")
    public String loginCheck(Login login, Model model){
        Cookie cookie = TokenCookie.getCookie();
        String token = cookie.getValue();
        String res = userService.singInUser(login, token);
        if(res.equals("Incorrect")){
            model.addAttribute("error", "Wrong login or password!!!");

            model.addAttribute("login", new Login());
            return "login";
        }
        else if(res.equals("Error")){
            model.addAttribute("error", "Please sing out firstly!!!");

            model.addAttribute("login", new Login());
            return "login";
        }
        else {
            cookie.setValue(res);
            cookie.setMaxAge(900);
            return "redirect:/";
        }
    }
    @GetMapping("/logOut")
    public String logOut(Model model){
        Cookie cookie = TokenCookie.getCookie();
        String token = cookie.getValue();
        Boolean signedOut = userService.LogOut(token);
        if(!signedOut){
            model.addAttribute("error", "Please Sign in firstly");
        }
        cookie.setValue("");
        cookie.setMaxAge(36500000);
        model.addAttribute("login", new Login());
        return "redirect:/login";
    }

    @GetMapping("/users")
    public String getUserList(Model model) {
        String token = TokenCookie.getCookie().getValue();
        if(!token.equals("")){
            Map<UserEntity,Boolean> userFriend = new HashMap<>();
            List<UserEntity> userList = userService.getUsers(token);
            for(UserEntity user: userList){
                userFriend.put(user,userService.checkOfSentRequest(token,user));
            }
            model.addAttribute("users",userFriend);
            model.addAttribute("userAuth", true);
            model.addAttribute("userInfo", userService.getUser(token));
             return "users";
        }
        return "redirect:/login";
    }
    @GetMapping("/friends")
    public String getUserFriends(Model model) {
        String token = TokenCookie.getCookie().getValue();
        if(!token.equals("")){
            List<UserEntity> userList = userService.getFriends(token);

            model.addAttribute("users",userList);
            model.addAttribute("userAuth", true);
            model.addAttribute("userInfo", userService.getUser(token));
            return "friends";
        }
        return "redirect:/login";
    }

    @PostMapping("/sendFriend")
    public String sendRequestForFriend(String login,Model model){
        String token = TokenCookie.getCookie().getValue();
        boolean sent = userService.sendRequestFriend(token, login);
        if(!sent)
            return "redirect:/login";

        return "redirect:/users";

    }

    @GetMapping("/notifications")
    public String showNotification(Model model){
        String token = TokenCookie.getCookie().getValue();
        if(!token.equals("")){
            List<FriendEntity> notifications = userService.getNotification(token);
            model.addAttribute("notifications",notifications);
            model.addAttribute("userAuth", true);
            model.addAttribute("userInfo", userService.getUser(token));
            return "notification";
        }

            return "redirect:/login";
    }

    @PostMapping("/acceptFriend")
    public String acceptRequestForFriend(String login,Model model){
        String token = TokenCookie.getCookie().getValue();
        if(!token.equals("")) {
            userService.acceptRequestFriend(token, login);
            return "redirect:/notifications";
        }
        return "redirect:/login";
    }



    @GetMapping("/profile/{login}")
    public String getUserProfile(@PathVariable String login,Model model){
        String token = TokenCookie.getCookie().getValue();
        UserEntity user = userService.getUserProfile(login,token);
        Map<PostEntity,List<CommentEntity>> postWithComments = new HashMap<>(2);
        UserEntity userInfo = null;
        if(!token.equals(""))
            userInfo = userService.getUser(token);

        if(user == null){
            model.addAttribute("error", "You can't see profile.");
            model.addAttribute("userInfo",userInfo);
            return "profileError";
        }

        List<PostEntity> posts = postService.getPostByUser(user);
        for (PostEntity post:posts) {
            List<CommentEntity> comments = commentService.getAllCommentsOfPost(post.getId());
            postWithComments.put(post,comments);
        }
        model.addAttribute("error", "");
        model.addAttribute("userInfo",userInfo);
        model.addAttribute("userAuth", true);
        model.addAttribute("user",user);
        model.addAttribute("posts", postWithComments);
        return "profile";
    }

    @GetMapping("/myProfile")
    public String getMyProfile(Model model){
        String token = TokenCookie.getCookie().getValue();
        if(!token.equals("")) {
            Map<PostEntity,List<CommentEntity>> postWithComments = new HashMap<>(2);
            UserEntity user = userService.getUser(token);
            List<PostEntity> posts = postService.getPostByUser(user);
            for (PostEntity post:posts) {
                List<CommentEntity> comments = commentService.getAllCommentsOfPost(post.getId());
                postWithComments.put(post,comments);
            }
            model.addAttribute("userAuth", true);
            model.addAttribute("userInfo", userService.getUser(token));
            model.addAttribute("posts", postWithComments);
            model.addAttribute("postEntity",new PostEntity());
            return "myProfile";
        }
        model.addAttribute("login",new Login());
        return "redirect:/login";

    }

    @PostMapping("/profile/visible/update")
    public String changeVisibleOfProfile(String visible){
        String token = TokenCookie.getCookie().getValue();
        if(token != null){
            userService.changeVisible(token, visible);
            return "redirect:/myProfile";
        }
        return "redirect:/login";
    }




}
