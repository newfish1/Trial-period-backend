package com.code.probationwork.dto.request;

import cn.hutool.http.body.MultipartBody;
import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
public class PublishRequest {
    MultipartFile image;
    String title;
    String content;
    Integer postType;
    Boolean isArgent;
    Boolean isAnonymity;
}
