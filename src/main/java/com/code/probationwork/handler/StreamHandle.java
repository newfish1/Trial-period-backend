package com.code.probationwork.handler;

import com.code.probationwork.entity.Post;
import com.code.probationwork.mapper.PostMapper;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class StreamHandle {
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private PostMapper postMapper;

    //stream相关常量
    private static final String STREAM_GROUP_NAME = "postGroup";
    //初始化消费者组
    private static final String STREAM_CONSUMER_NAME = "postConsumer1";
    private static final String STREAM_KEY = "postStream";
    //初始化stream消费者组
    @PostConstruct
    public void initConsumerGroup(){
        try{
            redisTemplate.opsForStream().createGroup(STREAM_KEY,STREAM_GROUP_NAME);
        }catch (Exception e){
            //如果组已存在则忽略
        }
    }
    @Scheduled(fixedDelay = 1000)
    @Async
    public void readStream(){
        try{
            //读取stream中的消息
            List<MapRecord<String, Object, Object>> messages = redisTemplate.opsForStream()
                    .read(Consumer.from(STREAM_GROUP_NAME,STREAM_CONSUMER_NAME),
                            StreamReadOptions.empty().count(10).block(Duration.ofSeconds(1)),
                            StreamOffset.create(STREAM_KEY, ReadOffset.lastConsumed()));
            if(!messages.isEmpty()&&messages!=null){
                for(MapRecord<String, Object, Object> message:messages){
                    //处理消息
                    processMessage(message);
                }
            }


        }catch (Exception e){
            log.error("读取stream消息失败",e);
        }
    }
    //处理消息
    private void processMessage(MapRecord<String, Object, Object> message) {
        Post post = null;
        try {
            Map<Object, Object> value = message.getValue();
            post = Post.builder()
                    .accountName((String) value.get("accountName"))
                    .title((String) value.get("title"))
                    .content((String) value.get("content"))
                    .reportType((Integer) value.get("reportType"))
                    .isUrgent((Integer) value.get("isUrgent"))
                    .isAnonymity((Integer) value.get("isAnonymity"))
                    .imageUrl((String) value.get("imageUrl"))
                    .build();
            postMapper.insert(post);
            //确认消息已被处理
            redisTemplate.opsForStream().acknowledge(STREAM_KEY, STREAM_GROUP_NAME, message.getId());
        } catch (Exception e) {
            postMapper.insert(post);
            redisTemplate.opsForStream().acknowledge(STREAM_KEY, STREAM_GROUP_NAME, message.getId());
        }
    }
}
