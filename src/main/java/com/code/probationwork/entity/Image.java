package com.code.probationwork.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

//图片实体类
@TableName(value ="image")
@Data
@Builder
public class Image {
    private String imageName;

    private String imagePath;

    private String imageUrl;

    private String imageHash;
}