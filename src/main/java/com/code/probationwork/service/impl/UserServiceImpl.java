package com.code.probationwork.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.code.probationwork.dto.request.EditInformationRequest;
import com.code.probationwork.dto.request.LoginRequest;
import com.code.probationwork.dto.response.LoginResponse;
import com.code.probationwork.entity.User;
import com.code.probationwork.exception.MyException;
import com.code.probationwork.mapper.UserMapper;
import com.code.probationwork.service.UserService;
import com.code.probationwork.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import com.code.probationwork.constant.ExceptionEnum;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
//用户服务实现类
public class UserServiceImpl implements UserService {
    @Resource
    private UserMapper userMapper;
    @Resource
    private RedisTemplate<String,Object> redisTemplate;
    @Resource
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public void register(User user) {
        LambdaQueryWrapper<User> userQueryWrapper = new LambdaQueryWrapper<>();
        userQueryWrapper.eq(User::getAccountName,user.getAccountName());
        User user1 = userMapper.selectOne(userQueryWrapper);
        if(user1!=null){
            throw new MyException(ExceptionEnum.REPEAT_REGISTER);
        }
        //校验账号格式，只能为数字
        if(!user.getAccountName().matches("\\d+")){
            throw new MyException(ExceptionEnum.USERNAME_DIGIT_ERROR);
        }
        //校验密码长度，必须为8-16位
        if(user.getPassword().length()<8||user.getPassword().length()>16){
            throw new MyException(ExceptionEnum.PASSWORD_LENGTH_ERROR);
        }
        //校验用户类型，只能为1、2、3
        if((user.getUserType()!=1&&user.getUserType()!=2&&user.getUserType()!=3)){
            throw new MyException(ExceptionEnum.TYPE_ERROR);
        }
        //校验邮箱格式
        if(!user.getEmail().matches("^[a-zA-Z0-9_.-]+@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*\\.[a-zA-Z0-9]{2,6}$")){
            throw new MyException(ExceptionEnum.EMAIL_FORMAT_ERROR);
        }
        //密码加密
        String password = user.getPassword();
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(11);
        password = bCryptPasswordEncoder.encode(password);
        user.setPassword(password);
        userMapper.insert(user);
    }

    //用户登录
    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        //查看是否存在
        LambdaQueryWrapper<User> userQueryWrapper = new LambdaQueryWrapper<>();
        userQueryWrapper.eq(User::getAccountName,loginRequest.getAccountName());
        User user = userMapper.selectOne(userQueryWrapper);
        if(user==null){
            throw new MyException(ExceptionEnum.NOT_FOUND_USER);
        }
        //校验密码是否正确
        if(!bCryptPasswordEncoder.matches(loginRequest.getPassword(),user.getPassword())){
            throw new MyException(ExceptionEnum.PASSWORD_ERROR);
        }
        //将验证身份信息存入token
        Map<String,Object> claims= new HashMap<>();
        claims.put("userId",user.getUserId());
        claims.put("accountName",user.getAccountName());
        String token = JwtUtil.genToken(claims);
        redisTemplate.opsForValue().set("user:token:"+user.getUserId(),token,24, TimeUnit.HOURS);
        //返回token
        return LoginResponse.builder().username(user.getUsername()).token(token).userType(user.getUserType()).build();
    }

    //用户退出登录
    @Override
    public void logout(HttpServletRequest request) {
        redisTemplate.delete("user:token:"+request.getAttribute("userId"));
    }

    //用户修改信息
    @Override
    public void edit(EditInformationRequest editInformationRequest, HttpServletRequest request) {
        //判断密码是否符合要求，并且修改
        if(editInformationRequest.getPassword()!=null){
            if(editInformationRequest.getPassword().length()<8||editInformationRequest.getPassword().length()>16){
                throw new MyException(ExceptionEnum.PASSWORD_LENGTH_ERROR);
            }
            String password = editInformationRequest.getPassword();
            password = bCryptPasswordEncoder.encode(password);
            editInformationRequest.setPassword(password);
        }

        //校验邮箱格式
        if(editInformationRequest.getEmail()!=null){
            if(!editInformationRequest.getEmail().matches("^[a-zA-Z0-9_.-]+@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*\\.[a-zA-Z0-9]{2,6}$")){
                throw new MyException(ExceptionEnum.EMAIL_FORMAT_ERROR);
            }
        }
        Integer userId = (Integer) request.getAttribute("userId");
        User user = userMapper.selectById(userId);

        //不为空则修改
        if(editInformationRequest.getUsername()!=null){
            user.setUsername(editInformationRequest.getUsername());
        }
        if(editInformationRequest.getPassword()!=null){
            user.setPassword(editInformationRequest.getPassword());
        }
        if(editInformationRequest.getEmail()!=null){
            user.setEmail(editInformationRequest.getEmail());
        }
        userMapper.updateById(user);
    }
}
