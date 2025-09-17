package com.code.probationwork.controller;


import com.code.probationwork.dto.request.PublishRequest;
import com.code.probationwork.mapper.PostMapper;
import com.code.probationwork.result.AjaxResult;
import com.code.probationwork.service.StuService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/student")
public class StuController {
    @Resource
    private StuService stuService;

    @PostMapping("/post")
    public AjaxResult<String> publish(PublishRequest publishRequest, HttpServletRequest request){
        stuService.publish(publishRequest, request);
        return AjaxResult.success("发布成功");
    }

}
