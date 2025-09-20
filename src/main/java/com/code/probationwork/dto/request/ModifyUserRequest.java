package com.code.probationwork.dto.request;


import lombok.Builder;
import lombok.Data;

//用户信息修改请求体
@Data
@Builder
public class ModifyUserRequest {
    private Integer operationType;
    private String accountName;
    private String username;
    private String password;
    private Integer userType;
    private String email;
}
