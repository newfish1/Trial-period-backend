package com.code.probationwork.service;

import com.code.probationwork.dto.request.AcceptPostRequest;
import com.code.probationwork.dto.request.MarkPostRequest;
import com.code.probationwork.dto.request.ReplyPostRequest;
import com.code.probationwork.dto.response.GetAllPostResponse;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface AdminService {
    List<GetAllPostResponse> getAllPost(HttpServletRequest request);
    void markPost(HttpServletRequest request, MarkPostRequest markPostRequest);
    void acceptPost(HttpServletRequest request, AcceptPostRequest acceptPostRequest);
    List<GetAllPostResponse> getAcceptPost(HttpServletRequest request);
    void cancelPost(HttpServletRequest request, AcceptPostRequest acceptPostRequest);
    void replyPost(HttpServletRequest request, ReplyPostRequest replyPostRequest);
}
