package com.code.probationwork.dto.response;


import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

//学生发布反馈响应体
@Data
@Builder
public class GetAllPostResponse {
    private Integer reportId;

    private String accountName;

    private String title;

    private String content;

    private String imageUrl;

    private Integer reportType;

    private Integer isUrgent;

    private Integer isAnonymity;

    private String reply;

    private String comment;

    private LocalDateTime postTime;
}
