package com.code.probationwork.controller;


import com.code.probationwork.dto.request.ModifyUserRequest;
import com.code.probationwork.dto.request.ReviewPostRequest;
import com.code.probationwork.dto.response.GetAllMarkResponse;
import com.code.probationwork.dto.response.GetAllUserResponse;
import com.code.probationwork.result.AjaxResult;
import com.code.probationwork.service.SuperAdminService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/superadmin")
public class SuperAdminController {
    @Resource
    private SuperAdminService superAdminService;

    //超级管理员获取所有用户记录
    @GetMapping("/getalluser")
    public AjaxResult<List<GetAllUserResponse>> getAllUser(HttpServletRequest request) {
        return AjaxResult.success(superAdminService.getAllUser(request));
    }

    //超级管理员对用户以及普通管理员信息进行增删改查
    @PostMapping("/modify")
    public AjaxResult<Object> modifyUser(HttpServletRequest request, @RequestBody ModifyUserRequest modifyUserRequest) {
        return AjaxResult.success(superAdminService.modifyUser(request, modifyUserRequest));
    }

    //获取所有标记信息
    @GetMapping("/getallmark")
    public AjaxResult<List<GetAllMarkResponse>> getAllMark(HttpServletRequest request) {
        return AjaxResult.success(superAdminService.getAllMark(request));
    }

    //审核帖子
    @PostMapping("/reviewpost")
    public AjaxResult<Void> reviewPost(HttpServletRequest request, @RequestBody ReviewPostRequest reviewPostRequest) {
        superAdminService.reviewPost(request, reviewPostRequest);
        return AjaxResult.success();
    }
}
