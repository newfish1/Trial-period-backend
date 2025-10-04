package com.code.probationwork.filter;

import com.code.probationwork.constant.ExceptionEnum;
import com.code.probationwork.exception.MyException;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@Order(1)
// 基于访问频率的黑名单过滤器
public class AccessLimitFilter implements Filter {

    // 注入RedisTemplate用于存储访问频率数据
    @Autowired
    private StringRedisTemplate redisTemplate;

    // 访问频率计数键前缀
    private static final String IP_ACCESS_COUNT = "ip:access:count:";
    // 黑名单键前缀
    private static final String IP_BLACKLIST = "ip:blacklist:";

    // 访问频率阈值：每秒最多访问次数
    private static final int ACCESS_THRESHOLD =10;

    // 黑名单有效期：单位秒
    private static final long BLACKLIST_EXPIRE_TIME = 300;

    // 计数过期时间：单位秒
    private static final long COUNT_EXPIRE_TIME = 1;

    private static String getRemoteAddr(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip != null && !ip.isEmpty()) {
            // X-Forwarded-For可能包含多个IP，取第一个
            return ip.split(",")[0].trim();
        }

        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        return ip == null ? request.getRemoteAddr() : ip;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        // 类型转换
        if (!(request instanceof HttpServletRequest req) || !(response instanceof HttpServletResponse res)) {
            chain.doFilter(request, response);
            return;
        }
        // 获取客户端IP地址
        String ip = getRemoteAddr(req);
        log.info("接收到来自IP: {} 的请求", ip);
        // 检查IP是否在黑名单中
        if (isIPBlacklisted(ip)) {
            log.info("IP: {} 在黑名单中，拒绝访问", ip);
            return;
        }
        // 增加访问计数
        long accessCount = incrementAccessCount(ip);
        // 检查是否超过访问频率阈值
        if (accessCount >= ACCESS_THRESHOLD) {
            // 将IP添加到黑名单
            addIPToBlacklist(ip);
            log.info("IP: {} 访问频率过高，已加入黑名单", ip);
            return;
        }
        chain.doFilter(request, response);
    }
    //用于判断IP是否在黑名单中
    private boolean isIPBlacklisted(String ip) {
        String blacklistKey = IP_BLACKLIST + ip;
        return redisTemplate.hasKey(blacklistKey);
    }

    // 将IP添加到黑名单
    private void addIPToBlacklist(String ip) {
        String blacklistKey = IP_BLACKLIST + ip;
        redisTemplate.opsForValue().set(blacklistKey, "true", BLACKLIST_EXPIRE_TIME, TimeUnit.SECONDS);
    }

    // 增加访问计数,并且设置过期时间为1秒
    private long incrementAccessCount(String ip) {
        String countKey = IP_ACCESS_COUNT + ip;
        // 增加计数并返回新值
        Long increment = redisTemplate.opsForValue().increment(countKey);
        // 如果是第一次访问，设置过期时间
        if (increment != null && increment == 1) {
            redisTemplate.expire(countKey, COUNT_EXPIRE_TIME, TimeUnit.SECONDS);
        }
        return increment != null ? increment : 0;
    }
}