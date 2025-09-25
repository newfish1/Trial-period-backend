package com.code.probationwork.controller;


import com.code.probationwork.dto.request.CommentRequest;
import com.code.probationwork.dto.request.PublishRequest;
import com.code.probationwork.dto.response.GetAllPostResponse;
import com.code.probationwork.result.AjaxResult;
import com.code.probationwork.service.StuService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/student")
public class StuController {
    @Resource
    private StuService stuService;

    //学生发布反馈
    @PostMapping("/report")
    public AjaxResult<String> publish(PublishRequest publishRequest, HttpServletRequest request){
        stuService.publish(publishRequest, request);
        return AjaxResult.success("发布成功");
    }

    //学生查看所有反馈
    @GetMapping("/get")
    public AjaxResult<List<GetAllPostResponse>> getAllPost(HttpServletRequest request){
        return AjaxResult.success(stuService.getAllPost(request));
    }

    //学生评论反馈
    @PutMapping("/comment")
    public AjaxResult<Void> comment(@RequestBody CommentRequest commentRequest, HttpServletRequest request){
        stuService.comment(commentRequest, request);
        return AjaxResult.success();
    }
}
