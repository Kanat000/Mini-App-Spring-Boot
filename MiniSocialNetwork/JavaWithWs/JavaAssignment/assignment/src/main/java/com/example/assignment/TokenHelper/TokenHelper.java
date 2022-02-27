package com.example.assignment.TokenHelper;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class TokenHelper {
    private final static long EXPIRATION_TIME = 864_000_000;
    private final static String SECRET = "Openspace";
    private static HashMap<String,String> BlackList = new HashMap<>(2);

    public static void addToBlackList(String login,String token){
        BlackList.put(login,token);
    }
    public static boolean blackListContains(String login){
        return BlackList.containsKey(login);
    }
    public static String getFromBlackList(String login){
        return BlackList.get(login);
    }
    public static void removeFromBlackList(String login){
        BlackList.remove(login);
    }
    public static String getToken(String login){
        return Jwts.builder().setSubject(login)
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS512, SECRET).compact();
    }
    public static String getLoginByToken(String token){
        return Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token).getBody().getSubject();
    }
}

