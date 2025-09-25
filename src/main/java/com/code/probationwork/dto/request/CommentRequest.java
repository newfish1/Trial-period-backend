package com.code.probationwork.dto.request;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CommentRequest {
    private Integer reportId;
    private String comment;
}
