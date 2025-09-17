package com.code.probationwork.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.code.probationwork.dto.request.EditInformationRequest;
import com.code.probationwork.dto.request.LoginRequest;
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
public class UserServiceImpl implements UserService {
    @Resource
    private UserMapper userMapper;
    @Resource
    private RedisTemplate<String,Object> redisTemplate;
    @Resource
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public void register(User user) {
        if(!user.getAccountName().matches("\\d+")){
            throw new MyException(ExceptionEnum.USERNAME_DIGIT_ERROR);
        }
        if(user.getPassword().length()<8||user.getPassword().length()>16){
            throw new MyException(ExceptionEnum.PASSWORD_LENGTH_ERROR);
        }
        if(user.getUserType()!=1&&user.getUserType()!=2&&user.getUserType()!=3){
            throw new MyException(ExceptionEnum.TYPE_ERROR);
        }
        //校验邮箱格式
        if(!user.getEmail().matches("^[a-zA-Z0-9_.-]+@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*\\.[a-zA-Z0-9]{2,6}$")){
            throw new MyException(ExceptionEnum.EMAIL_FORMAT_ERROR);
        }
        String password = user.getPassword();
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(11);
        password = bCryptPasswordEncoder.encode(password);
        LambdaQueryWrapper<User> userQueryWrapper = new LambdaQueryWrapper<>();
        userQueryWrapper.eq(User::getAccountName,user.getAccountName());
        User user1 = userMapper.selectOne(userQueryWrapper);
        if(user1!=null){
            throw new MyException(ExceptionEnum.REPEAT_REGISTER);
        }
        user.setPassword(password);
        userMapper.insert(user);
    }

    @Override
    public String login(LoginRequest loginRequest) {
        LambdaQueryWrapper<User> userQueryWrapper = new LambdaQueryWrapper<>();
        userQueryWrapper.eq(User::getAccountName,loginRequest.getAccountName());
        User user = userMapper.selectOne(userQueryWrapper);
        if(user==null){
            throw new MyException(ExceptionEnum.NOT_FOUND_USER);
        }
        if(!bCryptPasswordEncoder.matches(loginRequest.getPassword(),user.getPassword())){
            throw new MyException(ExceptionEnum.PASSWORD_ERROR);
        }
        Map<String,Object> claims= new HashMap<>();
        claims.put("userId",user.getUserId());
        claims.put("accountName",user.getAccountName());
        String token = JwtUtil.genToken(claims);
        redisTemplate.opsForValue().set("user:token:"+user.getUserId(),token,24, TimeUnit.HOURS);
        return token;
    }
    @Override
    public void logout(HttpServletRequest request) {
        redisTemplate.delete("user:token:"+request.getAttribute("userId"));
    }

    @Override
    public void edit(EditInformationRequest editInformationRequest, HttpServletRequest request) {
        if(editInformationRequest.getPassword()!=null){
            if(editInformationRequest.getPassword().length()<8||editInformationRequest.getPassword().length()>16){
                throw new MyException(ExceptionEnum.PASSWORD_LENGTH_ERROR);
            }
            String password = editInformationRequest.getPassword();
            password = bCryptPasswordEncoder.encode(password);
            editInformationRequest.setPassword(password);
        }
        if(editInformationRequest.getUserType()!=null) {
            if (editInformationRequest.getUserType() != 1 && editInformationRequest.getUserType() != 2 && editInformationRequest.getUserType() != 3) {
                throw new MyException(ExceptionEnum.TYPE_ERROR);
            }
        }
        //校验邮箱格式
        if(editInformationRequest.getEmail()!=null){
            if(!editInformationRequest.getEmail().matches("^[a-zA-Z0-9_.-]+@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*\\.[a-zA-Z0-9]{2,6}$")){
                throw new MyException(ExceptionEnum.EMAIL_FORMAT_ERROR);
            }
        }
        Integer userId = (Integer) request.getAttribute("userId");
        User user = userMapper.selectById(userId);
        if(editInformationRequest.getUsername()!=null){
            user.setUsername(editInformationRequest.getUsername());
        }
        if(editInformationRequest.getPassword()!=null){
            user.setPassword(editInformationRequest.getPassword());
        }
        if(editInformationRequest.getUserType()!=null){
            user.setUserType(editInformationRequest.getUserType());
        }
        if(editInformationRequest.getEmail()!=null){
            user.setEmail(editInformationRequest.getEmail());
        }
        userMapper.updateById(user);
    }
}
