package com.code.probationwork.service;

import com.code.probationwork.dto.request.CommentRequest;
import com.code.probationwork.dto.request.PublishRequest;
import com.code.probationwork.dto.response.GetAllPostResponse;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface StuService {
    void publish(PublishRequest publishRequest, HttpServletRequest request);
    List<GetAllPostResponse> getAllPost(HttpServletRequest request);
    void comment(CommentRequest commentRequest, HttpServletRequest request);

}
