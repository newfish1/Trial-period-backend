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
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.redis.core.RedisTemplate;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;


@Service
@Slf4j
//学生服务实现类
public class StuServiceImpl implements StuService {
    @Resource
    private UserMapper userMapper;
    @Resource
    private PostMapper postMapper;
    @Resource
    private ImageUtil imageUtil;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public void publish(PublishRequest publishRequest, HttpServletRequest request) {
//        String imageUrl = null;
//        String accountName=(String) request.getAttribute("accountName");
//        //保存图片
//        if (publishRequest.getImage() != null && !publishRequest.getImage().isEmpty() && publishRequest.getImage().getSize() > 0) {
//            imageUrl = imageUtil.saveImage(publishRequest.getImage());
//        }
//        Post post = Post.builder()
//                .title(publishRequest.getTitle())
//                .content(publishRequest.getContent())
//                .reportType(publishRequest.getReportType())
//                .isUrgent(publishRequest.getIsUrgent()?1:0)
//                .isAnonymity(publishRequest.getIsAnonymity()?1:0)
//                .imageUrl(imageUrl)
//                .accountName(accountName)
//                .build();
//        postMapper.insert(post);
//    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void publish(PublishRequest publishRequest, HttpServletRequest request) {
        if(publishRequest.getContent()==null||publishRequest.getIsUrgent()==null
                ||publishRequest.getReportType()==null||publishRequest.getIsAnonymity()==null){
            throw new MyException(ExceptionEnum.NULL_MESSAGE);
        }
        String imageUrl = null;
        String accountName=(String) request.getAttribute("accountName");
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getAccountName, accountName));

        //保存图片
        if (publishRequest.getImage() != null && !publishRequest.getImage().isEmpty() && publishRequest.getImage().getSize() > 0) {
            imageUrl = imageUtil.saveImage(publishRequest.getImage());
        }
        Post post = Post.builder()
                .title(publishRequest.getTitle())
                .content(publishRequest.getContent())
                .reportType(publishRequest.getReportType())
                .isUrgent(publishRequest.getIsUrgent()?1:0)
                .isAnonymity(publishRequest.getIsAnonymity()?1:0)
                .imageUrl(imageUrl)
                .accountName(accountName)
                .build();
        Map<String,Object> postData = new HashMap<>();
        postData.put("accountName",accountName);
        postData.put("title",publishRequest.getTitle());
        postData.put("content",publishRequest.getContent());
        postData.put("reportType",publishRequest.getReportType());
        postData.put("isUrgent",publishRequest.getIsUrgent()?1:0);
        postData.put("isAnonymity",publishRequest.getIsAnonymity()?1:0);
        postData.put("imageUrl",imageUrl);
        postData.put("email",user.getEmail());
        try{
            //将帖子数据添加到stream中，并且设置最大长度为1000
            RecordId recordId = redisTemplate.opsForStream().add("postStream",postData);
            //限定10000条
            redisTemplate.opsForStream().trim("postStream",10000);
        }catch (Exception e){
            log.info("发布帖子到stream失败");
            try{
                postMapper.insert(post);
            }catch (Exception e1){
                throw new MyException(ExceptionEnum.PUBLISH_FAILED);
            }
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
                        .reportId(post.getReportId())
                        .title(post.getTitle())
                        .content(post.getContent())
                        .reportType(post.getReportType())
                        .isUrgent(post.getIsUrgent())
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
        Post post = postMapper.selectById(commentRequest.getReportId());
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
