package com.code.probationwork.util;

import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.code.probationwork.constant.ExceptionEnum;
import com.code.probationwork.entity.Image;
import com.code.probationwork.exception.MyException;
import com.code.probationwork.mapper.ImageMapper;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Component
public class ImageUtil {
    @Resource
    private ImageMapper imageMapper;
    @Value("${save.path}")
    private String savePath;
    @Value("${base.url}")
    private String baseUrl;

    public String saveImage(MultipartFile file) {
        try {
            // 计算图片哈希值，用于判断是否已存在
            byte[] fileBytes = file.getBytes();
            String fileHash = DigestUtil.md5Hex(fileBytes);
            // 检查是否已存在相同哈希值的图片
            Image existingImage = imageMapper.selectOne(new LambdaQueryWrapper<Image>().eq(Image::getImageHash, fileHash));
            if (existingImage != null) {
                // 图片已存在，直接返回现有图片唯一文件名
                return existingImage.getImageUrl();
            }
            // 准备保存图片
            // 确保上传目录存在
            File saveDir = new File(savePath);
            if (!saveDir.exists()) {
                boolean mkdirs = saveDir.mkdirs();
                if (!mkdirs) {
                    throw new MyException(ExceptionEnum.IMAGE_SAVE_ERROR);
                }
            }
            // 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String imageName = UUID.randomUUID().toString() + extension;
            // 保存图片文件
            Path filePath = Paths.get(savePath, imageName);
            file.transferTo(filePath);
            //保存图片信息到数据库
            Image image = Image.builder()
                    .imageName(imageName)
                    .imagePath(filePath.toString())
                    .imageUrl(baseUrl + imageName)
                    .imageHash(fileHash)
                    .build();
            imageMapper.insert(image);
            return image.getImageUrl();

        } catch (Exception e) {
            throw new MyException(ExceptionEnum.IMAGE_SAVE_ERROR);
        }
    }

}
