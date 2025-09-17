package com.code.probationwork.service.impl;


import com.code.probationwork.dto.request.PublishRequest;
import com.code.probationwork.entity.Post;
import com.code.probationwork.mapper.ImageMapper;
import com.code.probationwork.mapper.PostMapper;
import com.code.probationwork.mapper.UserMapper;
import com.code.probationwork.service.StuService;
import com.code.probationwork.util.ImageUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StuServiceImpl implements StuService {
    @Resource
    private ImageMapper imageMapper;
    @Resource
    private PostMapper postMapper;
    @Resource
    private ImageUtil imageUtil;
    @Value("${save.path}")
    private String savePath;
    @Value("${base.url}")
    private String baseUrl;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void publish(PublishRequest publishRequest, HttpServletRequest request) {
        try{
            String imageName = null;
            String accountName=(String) request.getAttribute("accountName");
            //保存图片
            if (publishRequest.getImage() != null && !publishRequest.getImage().isEmpty()) {
               imageName = imageUtil.saveImage(publishRequest.getImage());
            }
            Post post = Post.builder()
                    .title(publishRequest.getTitle())
                    .content(publishRequest.getContent())
                    .postType(publishRequest.getPostType())
                    .isArgent(publishRequest.getIsArgent()?1:0)
                    .isAnonymity(publishRequest.getIsAnonymity()?1:0)
                    .imageName(imageName)
                    .build();
            if(!publishRequest.getIsAnonymity()){
                post.setAccountName(accountName);
            }
            postMapper.insert(post);
        }catch (Exception e){
            throw new RuntimeException("发布失败");
        }
    }
}
