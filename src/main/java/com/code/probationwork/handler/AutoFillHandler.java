package com.code.probationwork.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Component
//自动填充处理器
public class AutoFillHandler implements MetaObjectHandler {
    //东京时间
    ZonedDateTime shanghaiTime = ZonedDateTime.now(ZoneId.of("Asia/Shanghai"));
    @Override
    public void insertFill(MetaObject metaObject) {
        this.setFieldValByName("postTime", shanghaiTime.toLocalDateTime(), metaObject);
    }
    @Override
    public void updateFill(MetaObject metaObject) {
    }
}
