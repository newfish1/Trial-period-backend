package com.code.probationwork.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@TableName(value = "user")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
//用户实体类
public class User {
    @TableId(type= IdType.AUTO)
    private Integer userId;

    private String username;
    @NotNull
    private String accountName;
    @NotNull
    private String password;
    @NotNull
    private Integer userType;
    @NotNull
    private String email;
}
