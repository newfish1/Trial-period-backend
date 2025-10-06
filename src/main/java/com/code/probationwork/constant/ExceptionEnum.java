package com.code.probationwork.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
//配置枚举常量，用于自定义报错信息处理
public enum ExceptionEnum {
    SERVER_ERROR(202501, "系统错误，请重试"),
    PARAM_ERROR(202502, "参数错误"),
    NOT_FOUND_ERROR(202503, "请求路径异常"),
    USERNAME_DIGIT_ERROR(202504, "用户名必须为数字"),
    PASSWORD_LENGTH_ERROR(202505, "密码长度必须在8到16位之间"),
    TYPE_ERROR(202506, "用户类型错误"),
    REPEAT_REGISTER(202507, "用户名已存在"),
    EMAIL_FORMAT_ERROR(202508, "邮箱格式错误"),
    NOT_FOUND_USER(202509, "用户未创建"),
    PASSWORD_ERROR(202510, "密码错误"),
    IMAGE_SAVE_ERROR(202511, "图片保存失败"),
    PUBLISH_FAILED(202512, "发布失败"),
    NO_PERMISSION(202513, "无权限"),
    OPERATION_ERROR(202514, "操作错误"),
    CANNOT_DELETE_SUPERADMIN(202515, "不能删除超级管理员"),
    CANNOT_MODIFY_SUPERADMIN(202516, "不能修改超级管理员"),
    NULL_MESSAGE(202517, "信息不完整"),
    NOT_FOUND_POST(202518, "帖子不存在"),
    EMAIL_SEND_FAILED(202519, "邮件发送失败"),
    POST_ALREADY_ASSIGNED(202520, "帖子已被接单"),
    POST_ALREADY_REPLIED(202521, "反馈已被处理"),
    POST_NOT_REPLIED(202522, "帖子未被处理，无法评论"),
    LOCK_ERROR(202523, "其他管理员正在操作，稍后重试"),
    STREAM_ERROR(202524, "stream消息处理失败"),
    ;
    private final Integer errorCode;
    private final String errorMsg;

}
