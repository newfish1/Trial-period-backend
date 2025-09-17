package com.code.probationwork.dto.request;


import lombok.Data;

@Data
public class EditInformationRequest {
   private String username;
   private String password;
   private String email;
   private Integer userType ;
}
