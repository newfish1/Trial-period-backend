package com.code.probationwork.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

/**
 * @TableName image
 */
@TableName(value ="image")
@Data
@Builder
public class Image {
    private String imageName;

    private String imagePath;

    private String imageUrl;

    private String imageHash;
}