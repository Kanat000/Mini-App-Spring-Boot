package com.example.assignment.TokenHelper;

import javax.servlet.http.Cookie;

public class TokenCookie {
    private static Cookie cookie = new Cookie("token", "");

    public static Cookie getCookie() {
        return cookie;
    }

    public static void setCookie(Cookie cookie) {
        TokenCookie.cookie = cookie;
    }
}
