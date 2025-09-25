package com.code.probationwork.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;


@TableName(value ="post")
@Data
@Builder
//学生发布反馈实体类
public class Post {
    @TableId(type= IdType.AUTO)
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
    //是否是垃圾信息0不是 1标记 2是
    private Integer spam;

    private Integer assigner;
}