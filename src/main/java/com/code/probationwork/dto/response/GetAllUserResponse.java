package com.code.probationwork.dto.response;


import lombok.Builder;
import lombok.Data;

//用户信息查询响应体
@Data
@Builder
public class GetAllUserResponse {
    private Integer userId;
    private String accountName;
    private String username;
    private String email;
    private Integer userType;


}
