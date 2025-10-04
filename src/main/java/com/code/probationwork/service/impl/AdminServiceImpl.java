package com.code.probationwork.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.code.probationwork.constant.ExceptionEnum;
import com.code.probationwork.dto.request.AcceptPostRequest;
import com.code.probationwork.dto.request.MarkPostRequest;
import com.code.probationwork.dto.request.ReplyPostRequest;
import com.code.probationwork.dto.response.GetAllPostResponse;
import com.code.probationwork.entity.Post;
import com.code.probationwork.entity.User;
import com.code.probationwork.exception.MyException;
import com.code.probationwork.mapper.PostMapper;
import com.code.probationwork.mapper.UserMapper;
import com.code.probationwork.service.AdminService;
import com.code.probationwork.util.SendEmailUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
//管理员服务实现类
public class AdminServiceImpl implements AdminService {
    @Resource
    private PostMapper postMapper;
    @Resource
    private UserMapper userMapper;
    @Resource
    private SendEmailUtil sendEmailUtil;

    @Override
    //管理员查看所有帖子
    public List<GetAllPostResponse> getAllPost(HttpServletRequest request) {
        //获取当前登录用户的id，判断其是否有权限
        Integer userId = (Integer) request.getAttribute("userId");
        User user=userMapper.selectById(userId);
        if(user.getUserType()==1){
            throw new MyException(ExceptionEnum.NO_PERMISSION);
        }
        //按时间升序返回所有帖子
        LambdaQueryWrapper<Post> queryWrapper1 = new LambdaQueryWrapper<Post>()
                .orderByAsc(Post::getPostTime)
                .eq(Post::getSpam,0);
        List<Post> posts = postMapper.selectList(queryWrapper1);
        List<GetAllPostResponse> allPosts = posts.stream()
               .map(post -> GetAllPostResponse.builder()
               .reportId(post.getReportId())
               .accountName(post.getIsAnonymity()==1?"匿名用户":post.getAccountName())
               .title(post.getTitle())
               .content(post.getContent())
               .reportType(post.getReportType())
               .isUrgent(post.getIsUrgent())
               .isAnonymity(post.getIsAnonymity())
               .postTime(post.getPostTime())
               .reply(post.getReply())
               .comment(post.getComment())
               .imageUrl(post.getImageUrl())
               .build()).collect(Collectors.toList());
        return allPosts;
    }

//    @Override
//    public Page<GetAllPostResponse> getAllPost(HttpServletRequest request, Integer pageNum, Integer pageSize) {
//        // 获取当前登录用户的id，判断其是否有权限
//        Integer userId = (Integer) request.getAttribute("userId");
//        User user = userMapper.selectById(userId);
//        if(user.getUserType() == 1){
//            throw new MyException(ExceptionEnum.NO_PERMISSION);
//        }
//
//        // 创建分页对象
//        Page<Post> page = new Page<>(pageNum, pageSize);
//
//        // 按时间升序分页查询所有非垃圾帖子
//        LambdaQueryWrapper<Post> queryWrapper = new LambdaQueryWrapper<Post>()
//                .orderByAsc(Post::getPostTime)
//                .eq(Post::getSpam, 0);
//
//        Page<Post> postPage = postMapper.selectPage(page, queryWrapper);
//
//        // 转换为响应对象的分页
//        return postPage.convert(post -> GetAllPostResponse.builder()
//                .reportId(post.getReportId())
//                .accountName(post.getIsAnonymity() == 1 ? "匿名用户" : post.getAccountName())
//                .title(post.getTitle())
//                .content(post.getContent())
//                .reportType(post.getReportType())
//                .isUrgent(post.getIsUrgent())
//                .isAnonymity(post.getIsAnonymity())
//                .postTime(post.getPostTime())
//                .reply(post.getReply())
//                .comment(post.getComment())
//                .imageUrl(post.getImageUrl())
//                .build());
//    }


    //管理员对帖子进行标记
    @Override
    public void markPost(HttpServletRequest request, MarkPostRequest markPostRequest) {
        //获取当前登录用户的id，判断其是否有权限
        Integer userId = (Integer) request.getAttribute("userId");
        User user = userMapper.selectById(userId);
        if (user.getUserType() == 1) {
            throw new MyException(ExceptionEnum.NO_PERMISSION);
        }
        //判断帖子是否存在
        Post post = postMapper.selectById(markPostRequest.getReportId());
        if (post == null) {
            throw new MyException(ExceptionEnum.NOT_FOUND_POST);
        }
        if(post.getReply()!=null){
            throw new MyException(ExceptionEnum.POST_ALREADY_REPLIED);
        }
        //标记为垃圾信息
        post.setSpam(1);
        postMapper.updateById(post);
    }

    //管理员接单
    @Override
    public void acceptPost(HttpServletRequest request, AcceptPostRequest acceptPostRequest) {
        //获取当前登录用户的id，判断其是否有权限
        Integer userId = (Integer) request.getAttribute("userId");
        User user = userMapper.selectById(userId);
        if (user.getUserType() == 1) {
            throw new MyException(ExceptionEnum.NO_PERMISSION);
        }
        //判断帖子是否存在
        Post post = postMapper.selectById(acceptPostRequest.getReportId());
        if (post == null) {
            throw new MyException(ExceptionEnum.NOT_FOUND_POST);
        }
        //判断帖子是否已经被接单
        if (post.getAssigner() != null) {
            throw new MyException(ExceptionEnum.POST_ALREADY_ASSIGNED);
        }
        //接单
        post.setAssigner(userId);
        postMapper.updateById(post);
    }

    //管理员撤销接单
    @Override
    public void cancelPost(HttpServletRequest request, AcceptPostRequest acceptPostRequest) {
        //获取当前登录用户的id，判断其是否有权限
        Integer userId = (Integer) request.getAttribute("userId");
        User user = userMapper.selectById(userId);
        if (user.getUserType() == 1) {
            throw new MyException(ExceptionEnum.NO_PERMISSION);
        }
        //判断帖子是否存在
        Post post = postMapper.selectById(acceptPostRequest.getReportId());
        if (post == null) {
            throw new MyException(ExceptionEnum.NOT_FOUND_POST);
        }
        //判断帖子是否是自己的
        if (!post.getAssigner().equals(userId)) {
            throw new MyException(ExceptionEnum.NO_PERMISSION);
        }
        //判断帖子是否回复过
        if(post.getReply()!=null){
            throw new MyException(ExceptionEnum.POST_ALREADY_REPLIED);
        }
        //撤销接单
        LambdaUpdateWrapper<Post> updateWrapper = new LambdaUpdateWrapper<Post>()
                .set(Post::getAssigner, null)
                .eq(Post::getReportId, acceptPostRequest.getReportId());
        postMapper.update(null, updateWrapper);
    }

    //管理员获得自己的接单信息
    @Override
    public List<GetAllPostResponse> getAcceptPost(HttpServletRequest request) {
        //获取当前登录用户的id，判断其是否有权限
        Integer userId = (Integer) request.getAttribute("userId");
        User user = userMapper.selectById(userId);
        if (user.getUserType() == 1) {
            throw new MyException(ExceptionEnum.NO_PERMISSION);
        }
        //查询所有自己接单的帖子
        LambdaQueryWrapper<Post> queryWrapper = new LambdaQueryWrapper<Post>()
                .eq(Post::getAssigner, userId)
                .orderByAsc(Post::getPostTime)
                .orderByDesc(Post::getIsUrgent);
        List<Post> posts = postMapper.selectList(queryWrapper);
        return posts.stream().map(post -> GetAllPostResponse.builder()
                .reportId(post.getReportId())
                .accountName(post.getIsAnonymity() == 1 ? "匿名用户" : post.getAccountName())
                .title(post.getTitle())
                .content(post.getContent())
                .reportType(post.getReportType())
                .isUrgent(post.getIsUrgent())
                .isAnonymity(post.getIsAnonymity())
                .postTime(post.getPostTime())
                .reply(post.getReply())
                .comment(post.getComment())
                .imageUrl(post.getImageUrl())
                .build()).collect(Collectors.toList());
    }

    //管理员回复
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void replyPost(HttpServletRequest request, ReplyPostRequest replyPostRequest) {
        //获取当前登录用户的id，判断其是否有权限
        Integer userId = (Integer) request.getAttribute("userId");
        User user = userMapper.selectById(userId);
        if (user.getUserType() == 1) {
            throw new MyException(ExceptionEnum.NO_PERMISSION);
        }
        //判断帖子是否存在
        Post post = postMapper.selectById(replyPostRequest.getReportId());
        if (post == null) {
            throw new MyException(ExceptionEnum.NOT_FOUND_POST);
        }
        //判断帖子是否已经被回复
        if (post.getReply() != null) {
            throw new MyException(ExceptionEnum.POST_ALREADY_REPLIED);
        }
        //回复帖子
        post.setReply(replyPostRequest.getReply());
        postMapper.updateById(post);
        try{
            //发送邮件通知用户
            User user2 = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getAccountName, post.getAccountName()));
            sendEmailUtil.sendEmail(user2.getEmail(), "服务平台反馈处理", "您的反馈已被回复处理，前往平台查看");
        }catch (Exception e){
            throw new MyException(ExceptionEnum.EMAIL_SEND_FAILED);
        }
    }
}
