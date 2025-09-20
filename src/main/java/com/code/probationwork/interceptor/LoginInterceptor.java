package com.code.probationwork.interceptor;


import com.code.probationwork.exception.MyException;
import com.code.probationwork.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;


@Component
//jwt登录拦截器
public class LoginInterceptor implements HandlerInterceptor {
    private final RedisTemplate<String, Object> redisTemplate;

    public LoginInterceptor(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//        Map<String, Set<String>> excludeRules = new HashMap<>();
//        excludeRules.put("/api/student/post", Collections.singleton("GET"));
//        String requestURI = request.getRequestURI();
//        String method = request.getMethod();
//        if (excludeRules.containsKey(requestURI) && excludeRules.get(requestURI).contains(method)) {
//            return true;
//        }
        String token = request.getHeader("Authorization");
        try {
            Map<String,Object> claims = JwtUtil.parseToken(token);
            Integer userId = (Integer) claims.get("userId");
            request.setAttribute("userId",userId);
            String accountName = (String) claims.get("accountName");
            request.setAttribute("accountName",accountName);
            if(redisTemplate.opsForValue().get("user:token:"+userId)==null){
                throw new MyException(402,"登录认证失败，请重新登录");
            }
           return true;
        }catch (Exception e){
            throw new MyException(401,"登录认证失败，请重新登录");
        }
    }
}
