package com.code.probationwork.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.code.probationwork.dto.request.AcceptPostRequest;
import com.code.probationwork.dto.request.MarkPostRequest;
import com.code.probationwork.dto.request.ReplyPostRequest;
import com.code.probationwork.dto.response.GetAllPostResponse;
import com.code.probationwork.result.AjaxResult;
import com.code.probationwork.service.AdminService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    @Resource
    private AdminService adminService;

    //管理员获取所有反馈记录
    @GetMapping("/getallpost")
    public AjaxResult<Page<GetAllPostResponse>> getAllPost(HttpServletRequest request, @RequestParam(defaultValue = "1") Integer pageNum) {
        Integer pageSize = 10;
        return AjaxResult.success(adminService.getAllPost(request, pageNum, pageSize));
    }

    //管理员对帖子进行标记
    @PostMapping("/markpost")
    public AjaxResult<String> markPost(HttpServletRequest request, @RequestBody MarkPostRequest markPostRequest) {
        adminService.markPost(request, markPostRequest);
        return AjaxResult.success("标记成功");
    }

    //管理员接单
    @PutMapping("/acceptpost")
    public AjaxResult<String> acceptPost(HttpServletRequest request, @RequestBody AcceptPostRequest acceptPostRequest) {
        adminService.acceptPost(request, acceptPostRequest);
        return AjaxResult.success();
    }

    //管理员查看自己的接单信息
    @GetMapping("/getacceptpost")
    public AjaxResult<List<GetAllPostResponse>> getAcceptPost(HttpServletRequest request){
        return AjaxResult.success(adminService.getAcceptPost(request));
    }

    //管理员取消接单
    @PutMapping("/cancel")
    public AjaxResult<Void> cancelPost(HttpServletRequest request, @RequestBody AcceptPostRequest acceptPostRequest){
        adminService.cancelPost(request, acceptPostRequest);
        return AjaxResult.success();
    }

    //管理员回复
    @PostMapping("/reply")
    public AjaxResult<String> replyPost(HttpServletRequest request, @RequestBody ReplyPostRequest replyPostRequest){
        adminService.replyPost(request, replyPostRequest);
        return AjaxResult.success("回复成功");
    }
}
