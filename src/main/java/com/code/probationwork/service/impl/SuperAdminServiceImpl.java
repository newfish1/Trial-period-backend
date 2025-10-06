package com.code.probationwork.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.code.probationwork.constant.ExceptionEnum;
import com.code.probationwork.dto.request.ModifyUserRequest;
import com.code.probationwork.dto.request.ReviewPostRequest;
import com.code.probationwork.dto.response.GetAllMarkResponse;
import com.code.probationwork.dto.response.GetAllPostResponse;
import com.code.probationwork.dto.response.GetAllUserResponse;
import com.code.probationwork.entity.Post;
import com.code.probationwork.entity.User;
import com.code.probationwork.exception.MyException;
import com.code.probationwork.mapper.PostMapper;
import com.code.probationwork.mapper.UserMapper;
import com.code.probationwork.service.SuperAdminService;
import com.code.probationwork.util.SendEmailUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class SuperAdminServiceImpl implements SuperAdminService {
    @Resource
    private UserMapper userMapper;
    @Resource
    private PostMapper postMapper;
    @Resource
    private SendEmailUtil sendEmailUtil;
    @Resource
    private RedissonClient redissonClient;

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
                .orderByDesc(Post::getPostTime)
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
    //超级管理员查看所有用户
    @Override
    public List<GetAllUserResponse> getAllUser(HttpServletRequest request) {
        //获取当前登录用户的id，判断其是否有权限
        Integer userId = (Integer) request.getAttribute("userId");
        User user=userMapper.selectById(userId);
        if(user.getUserType()!=3){
            throw new MyException(ExceptionEnum.NO_PERMISSION);
        }
        //获取所有用户信息
        List<User> users=userMapper.selectList(null);
        return users.stream().map(
                User->GetAllUserResponse.builder().
                        userId(User.getUserId()).
                        accountName(User.getAccountName()).
                        username(User.getUsername()).
                        userType(User.getUserType()).
                        email(User.getEmail()).
                        build()
        ).collect(Collectors.toList());
    }

    @Override
    public Object modifyUser(HttpServletRequest request, ModifyUserRequest modifyUserRequest) {
        //获取当前登录用户的id，判断其是否有权限
        Integer userId = (Integer) request.getAttribute("userId");
        User user = userMapper.selectById(userId);
        if (user.getUserType() != 3) {
            throw new MyException(ExceptionEnum.NO_PERMISSION);
        }
        Integer operationType = modifyUserRequest.getOperationType();
        //增加一个用户
        if (operationType == 1) {
            //判断用户是否存在
            User user1 = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getAccountName, modifyUserRequest.getAccountName()));
            if (user1 != null) {
                throw new MyException(ExceptionEnum.REPEAT_REGISTER);
            }
            //判断数据是否有null
            if (modifyUserRequest.getAccountName() == null || modifyUserRequest.getPassword() == null || modifyUserRequest.getUsername() == null || modifyUserRequest.getUserType() == null || modifyUserRequest.getEmail() == null) {
                throw new MyException(ExceptionEnum.NULL_MESSAGE);
            }
            //密码加密
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            String encode = passwordEncoder.encode(modifyUserRequest.getPassword());
            User user2 = User.builder().
                    accountName(modifyUserRequest.getAccountName()).
                    password(encode).
                    username(modifyUserRequest.getUsername()).
                    userType(modifyUserRequest.getUserType()).
                    email(modifyUserRequest.getEmail()).
                    build();
            userMapper.insert(user2);
            return "增加成功";
        }
        //删除一个用户
        else if (operationType == 2) {
            User u=userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getAccountName, modifyUserRequest.getAccountName()));
            if(u==null){
                throw new MyException(ExceptionEnum.NOT_FOUND_USER);
            }
            if(u.getUserType()==3){
                throw new MyException(ExceptionEnum.CANNOT_DELETE_SUPERADMIN);
            }
            userMapper.delete(new LambdaQueryWrapper<User>().eq(User::getAccountName, modifyUserRequest.getAccountName()));
            return "删除成功";
        }

        //修改一个用户
        else if (operationType == 3) {
            User user3 = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getAccountName, modifyUserRequest.getAccountName()));
            if (user3 == null) {
                throw new MyException(ExceptionEnum.NOT_FOUND_USER);
            }
            //获取锁对象
            String lockKey="lock:accountName:"+modifyUserRequest.getAccountName();
            RLock lock=redissonClient.getLock(lockKey);
            try {
                //尝试获取锁，设置等待获取时间5s，锁的超时时间为1h
                boolean locked=lock.tryLock(5, 3600, TimeUnit.SECONDS);
                if (!locked) {
                    throw new MyException(ExceptionEnum.LOCK_ERROR);
                }
                //判断是否需要修改的为null，不为null则修改对应信息
                if (modifyUserRequest.getAccountName() != null) {
                    user3.setAccountName(modifyUserRequest.getAccountName());
                }
                if (modifyUserRequest.getUsername() != null) {
                    user3.setUsername(modifyUserRequest.getUsername());
                }
                if (modifyUserRequest.getUsername() != null) {
                    user3.setUsername(modifyUserRequest.getUsername());
                }
                if (modifyUserRequest.getPassword() != null) {
                    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
                    String encode = passwordEncoder.encode(modifyUserRequest.getPassword());
                    user3.setPassword(encode);
                }
                if (modifyUserRequest.getUserType() != null) {
                    user3.setUserType(modifyUserRequest.getUserType());
                }
                if (modifyUserRequest.getEmail() != null) {
                    user3.setEmail(modifyUserRequest.getEmail());
                }
                userMapper.updateById(user3);
                return "修改成功";
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                lock.unlock();
            }
        }

        //查看accountName用户
        else if (operationType == 4) {
            User user4 = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getAccountName, modifyUserRequest.getAccountName()));
            if (user4 == null) {
                throw new MyException(ExceptionEnum.NOT_FOUND_USER);
            }
            return GetAllUserResponse.builder().
                    userId(user4.getUserId()).
                    accountName(user4.getAccountName()).
                    username(user4.getUsername()).
                    userType(user4.getUserType()).
                    email(user4.getEmail()).
                    build();
        }
        else {
            throw new MyException(ExceptionEnum.OPERATION_ERROR);
        }
    }

    //获取所有标记信息
    @Override
    public List<GetAllMarkResponse> getAllMark(HttpServletRequest request) {
        //获取当前登录用户的id，判断其是否有权限
        Integer userId = (Integer) request.getAttribute("userId");
        User user=userMapper.selectById(userId);
        if(user.getUserType()!=3){
            throw new MyException(ExceptionEnum.NO_PERMISSION);
        }
        //获取所有标记信息
        List<Post> posts=postMapper.selectList(new LambdaQueryWrapper<Post>().eq(Post::getSpam,1));
        return posts.stream().map(
                post->GetAllMarkResponse.builder().
                        reportId(post.getReportId()).
                        accountName(post.getIsAnonymity()==1?"匿名用户":post.getAccountName()).
                        title(post.getTitle()).
                        content(post.getContent()).
                        reportType(post.getReportType()).
                        isUrgent(post.getIsUrgent()).
                        isAnonymity(post.getIsAnonymity()).
                        postTime(post.getPostTime()).
                        reply(post.getReply()).
                        comment(post.getComment()).
                        imageUrl(post.getImageUrl()).
                        spam(post.getSpam()).
                        build()
        ).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reviewPost(HttpServletRequest request, ReviewPostRequest reviewPostRequest) {
        Integer userId = (Integer) request.getAttribute("userId");
        User user = userMapper.selectById(userId);
        if (user.getUserType() != 3) {
            throw new MyException(ExceptionEnum.NO_PERMISSION);
        }
        //审核帖子
        Post post = postMapper.selectById(reviewPostRequest.getReportId());
        if (post == null) {
            throw new MyException(ExceptionEnum.NOT_FOUND_POST);
        }
        post.setSpam(reviewPostRequest.getSpam());
        postMapper.updateById(post);
        //如果不通过回复帖子
        if (reviewPostRequest.getSpam() == 2) {
            post.setReply("请您在提交反馈时确保内容有效性和准确性，感谢您的理解与配合。如有异议，请重新反馈。");
            postMapper.updateById(post);
            //发送邮件通知用户
            try {
                User user2 = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getAccountName, post.getAccountName()));
                sendEmailUtil.sendEmail(user2.getEmail(), "反馈处理", "您的反馈已被审核，未通过,请重新反馈");
            } catch (Exception e) {
                throw new MyException(ExceptionEnum.EMAIL_SEND_FAILED);
            }
        }
    }
}
