package com.code.probationwork.service;

import com.code.probationwork.dto.request.PublishRequest;
import jakarta.servlet.http.HttpServletRequest;

public interface StuService {
    void publish(PublishRequest publishRequest, HttpServletRequest request);
}
