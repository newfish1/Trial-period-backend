package com.code.probationwork.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponse {
    private String username;
    private String token;
    private Integer userType;
}
