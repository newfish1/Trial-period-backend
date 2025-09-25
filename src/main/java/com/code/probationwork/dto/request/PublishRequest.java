package com.code.probationwork.dto.request;

import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

//学生发布反馈请求体
@Data
@Builder
public class PublishRequest {
    MultipartFile image;
    String title;
    String content;
    Integer reportType;
    Boolean isUrgent;
    Boolean isAnonymity;
}
