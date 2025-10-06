package com.code.probationwork.service;

import com.code.probationwork.dto.request.ModifyUserRequest;
import com.code.probationwork.dto.request.ReviewPostRequest;
import com.code.probationwork.dto.response.GetAllMarkResponse;
import com.code.probationwork.dto.response.GetAllPostResponse;
import com.code.probationwork.dto.response.GetAllUserResponse;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface SuperAdminService {
    List<GetAllUserResponse> getAllUser(HttpServletRequest request);
    Object modifyUser(HttpServletRequest request, ModifyUserRequest modifyUserRequest);
    List<GetAllMarkResponse> getAllMark(HttpServletRequest request);
    void reviewPost(HttpServletRequest request, ReviewPostRequest reviewPostRequest);
     List<GetAllPostResponse> getAllPost(HttpServletRequest request);
}
