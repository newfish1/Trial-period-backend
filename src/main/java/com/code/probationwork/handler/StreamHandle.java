package com.code.probationwork.handler;

import com.code.probationwork.constant.ExceptionEnum;
import com.code.probationwork.entity.Post;
import com.code.probationwork.exception.MyException;
import com.code.probationwork.mapper.PostMapper;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.code.probationwork.util.SendEmailUtil;

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
    @Resource
    private SendEmailUtil sendEmailUtil;
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
    public void scheduledMethod(){
        readStream();
    }

    @Async("streamExecutor")
    public void readStream(){
        try {
            //读取stream中的消息
            List<MapRecord<String, Object, Object>> messages = redisTemplate.opsForStream()
                    .read(Consumer.from(STREAM_GROUP_NAME, STREAM_CONSUMER_NAME),
                            StreamReadOptions.empty().count(10).block(Duration.ofSeconds(10)),
                            StreamOffset.create(STREAM_KEY, ReadOffset.lastConsumed()));
            if (messages != null && !messages.isEmpty()) {
                for (MapRecord<String, Object, Object> message : messages) {
                    //处理消息
                    processMessage(message);
                }
            }
        }catch (Exception e){
            Boolean hasKey = redisTemplate.hasKey(STREAM_KEY);
            if (hasKey != null && !hasKey) {
                // 创建一个空的stream
                redisTemplate.opsForStream().add(STREAM_KEY, Map.of("init", "init"));
            }
            initConsumerGroup();
        }
    }
    @Async("messageExecutor")
    //处理消息
    public void processMessage(MapRecord<String, Object, Object> message) {
        int maxTry = 5;
        int countTry = 0;
        boolean success = false;
        while (!success && countTry < maxTry) {
            try {
                countTry++;
                Map<Object, Object> value = message.getValue();
                Post post = Post.builder()
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
                success = true;
            } catch (Exception e) {
                countTry++;
                log.info("处理stream消息失败");
                if (countTry == maxTry) {
                    break;
                }
            }
        }
        if (!success) {
            try {
                redisTemplate.opsForStream().acknowledge(STREAM_KEY, STREAM_GROUP_NAME, message.getId());
                Map<Object, Object> value = message.getValue();
                String email = (String) value.get("email");
                //最终发布失败则邮箱提醒
                if (email != null) {
                    sendEmailUtil.sendEmail(email, "发布失败", "您的帖子发布失败，请重新发布或者联系管理员");
                }
                if (!success) {
                    log.info("发布失败");
                }
            } catch (Exception e) {
                log.info("错误stream消息处理失败");
            }
        }
    }
}
