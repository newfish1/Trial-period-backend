package com.code.probationwork.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;

@Component
public class JwtUtil {
    public static String KEY;
    @Value("${jwt.key}")
    public void setKey(String key) {
        KEY = key;
    }

    public static String genToken(Map<String,Object> claims){
        return JWT.create()
                .withClaim("private_message",claims)
                .withExpiresAt(new Date(System.currentTimeMillis()+1000*60*60*24))
                .sign(Algorithm.HMAC256(KEY));
    }
    public static Map<String,Object> parseToken(String token){
        return JWT.require(Algorithm.HMAC256(KEY))
                .build()
                .verify(token)
                .getClaim("private_message")
                .asMap();
    }
}
