package com.code.probationwork.dto.response;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;


@Data
@Builder
public class GetAllMarkResponse {
    private Integer reportId;

    private String accountName;

    private String title;

    private String content;

    private Integer reportType;

    private Integer isUrgent;

    private Integer isAnonymity;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime postTime;

    private String reply;

    private String comment;

    private String imageUrl;

    private Integer spam;
}
