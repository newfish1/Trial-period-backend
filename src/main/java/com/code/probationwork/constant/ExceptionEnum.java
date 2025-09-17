package com.code.probationwork.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ExceptionEnum {
    SERVER_ERROR(202501, "系统错误，请重试"),
    PARAM_ERROR(202502, "参数错误"),
    NOT_FOUND_ERROR(202503, "资源不存在"),
    USERNAME_DIGIT_ERROR(202504, "用户名必须为数字"),
    PASSWORD_LENGTH_ERROR(202505, "密码长度必须在8到16位之间"),
    TYPE_ERROR(202506, "用户类型错误"),
    REPEAT_REGISTER(202507, "用户名已存在"),
    EMAIL_FORMAT_ERROR(202508, "邮箱格式错误"),
    NOT_FOUND_USER(202509, "用户未创建"),
    PASSWORD_ERROR(202510, "密码错误"),
    IMAGE_SAVE_ERROR(202511, "图片保存失败"),
    REDIS_CONNECTION_ERROR(202512, "Redis连接错误"),
    ;
    private final Integer errorCode;
    private final String errorMsg;

}
