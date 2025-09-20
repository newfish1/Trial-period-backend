package com.code.probationwork.dto.request;


import lombok.Data;

//用户信息编辑请求体
@Data
public class EditInformationRequest {
   private String username;
   private String password;
   private String email;
}
