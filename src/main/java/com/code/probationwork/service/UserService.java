package com.code.probationwork.service;

import com.code.probationwork.dto.request.EditInformationRequest;
import com.code.probationwork.dto.request.LoginRequest;
import com.code.probationwork.entity.User;
import jakarta.servlet.http.HttpServletRequest;

public interface UserService {
    void register(User user);
    String login(LoginRequest loginRequest);
    void logout(HttpServletRequest request);
    void edit(EditInformationRequest editInformationRequest, HttpServletRequest request);
}
