package com.code.probationwork.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.code.probationwork.constant.ExceptionEnum;
import com.code.probationwork.dto.request.CommentRequest;
import com.code.probationwork.dto.request.PublishRequest;
import com.code.probationwork.dto.response.GetAllPostResponse;
import com.code.probationwork.entity.Post;
import com.code.probationwork.entity.User;
import com.code.probationwork.exception.MyException;
import com.code.probationwork.mapper.PostMapper;
import com.code.probationwork.mapper.UserMapper;
import com.code.probationwork.service.StuService;
import com.code.probationwork.util.ImageUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
//学生服务实现类
public class StuServiceImpl implements StuService {
    @Resource
    private UserMapper userMapper;
    @Resource
    private PostMapper postMapper;
    @Resource
    private ImageUtil imageUtil;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void publish(PublishRequest publishRequest, HttpServletRequest request) {
        try{
            String imageUrl = null;
            String accountName=(String) request.getAttribute("accountName");
            //保存图片
            if (publishRequest.getImage() != null && !publishRequest.getImage().isEmpty()) {
                imageUrl = imageUtil.saveImage(publishRequest.getImage());
            }
            Post post = Post.builder()
                    .title(publishRequest.getTitle())
                    .content(publishRequest.getContent())
                    .postType(publishRequest.getPostType())
                    .isArgent(publishRequest.getIsArgent()?1:0)
                    .isAnonymity(publishRequest.getIsAnonymity()?1:0)
                    .imageUrl(imageUrl)
                    .accountName(accountName)
                    .build();
            postMapper.insert(post);
        }catch (Exception e){
            throw new MyException(ExceptionEnum.PUBLISH_FAILED);
        }
    }
    @Override
    public List<GetAllPostResponse> getAllPost(HttpServletRequest request) {
        String accountName=(String) request.getAttribute("accountName");
        LambdaQueryWrapper<Post> queryWrapper = new LambdaQueryWrapper<Post>()
                .eq(Post::getAccountName, accountName)
                .orderByDesc(Post::getPostTime);
        return postMapper.selectList(queryWrapper).stream()
                .map(post -> GetAllPostResponse.builder()
                        .postId(post.getPostId())
                        .title(post.getTitle())
                        .content(post.getContent())
                        .postType(post.getPostType())
                        .isArgent(post.getIsArgent())
                        .isAnonymity(post.getIsAnonymity())
                        .imageUrl(post.getImageUrl())
                        .accountName(post.getAccountName())
                        .postTime(post.getPostTime())
                        .reply(post.getReply())
                        .comment(post.getComment())
                        .build())
                .collect(Collectors.toList());
    }

    //学生评论回复
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void comment(CommentRequest commentRequest, HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        Post post = postMapper.selectById(commentRequest.getPostId());
        if(post==null){
            throw new MyException(ExceptionEnum.NOT_FOUND_POST);
        }
        User user = userMapper.selectById(userId);
        if(!post.getAccountName().equals(user.getAccountName())){
            throw new MyException(ExceptionEnum.NO_PERMISSION);
        }
        if(post.getReply()==null){
            throw new MyException(ExceptionEnum.POST_NOT_REPLIED);
        }
        post.setComment(commentRequest.getComment());
        postMapper.updateById(post);
    }
}
