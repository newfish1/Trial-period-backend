package com.code.probationwork.controller;


import com.code.probationwork.dto.request.EditInformationRequest;
import com.code.probationwork.dto.request.LoginRequest;
import com.code.probationwork.entity.User;
import com.code.probationwork.result.AjaxResult;
import com.code.probationwork.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/api/user")
public class UserController {
    @Resource
    private UserService userService;

    //用户注册
    @PostMapping("/reg")
    public AjaxResult<String> register(@RequestBody User user) {
        userService.register(user);
        return AjaxResult.success("注册成功！");
    }

    //用户登录
    @PostMapping("/login")
    public AjaxResult<String> login(@RequestBody LoginRequest loginRequest) {
        return AjaxResult.success(userService.login(loginRequest));
    }

    //用户退出登录
    @PostMapping("/logout")
    public AjaxResult<String> logout(HttpServletRequest request) {
        userService.logout(request);
        return AjaxResult.success("退出成功！");
    }

    //用户信息编辑
    @PutMapping("/edit")
    public AjaxResult<String> edit(@RequestBody EditInformationRequest editInformationRequest, HttpServletRequest request) {
        userService.edit(editInformationRequest,request);
        return AjaxResult.success("编辑成功！");
    }
}
