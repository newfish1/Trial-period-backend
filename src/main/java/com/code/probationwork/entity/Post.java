package com.code.probationwork.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

/**
 * @TableName post
 */
@TableName(value ="post")
@Data
@Builder
public class Post {
    @TableId(type= IdType.AUTO)
    private Integer postId;

    private String accountName;

    private String title;

    private String content;

    private Integer postType;

    private Integer isArgent;

    private Integer isAnonymity;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime postTime;

    private String reply;

    private String assessment;

    private String imageName;
}