package com.ymjrhk.rbac.service.impl;

import com.ymjrhk.rbac.constant.StatusConstant;
import com.ymjrhk.rbac.dto.UserLoginDTO;
import com.ymjrhk.rbac.entity.User;
import com.ymjrhk.rbac.mapper.UserMapper;
import com.ymjrhk.rbac.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.security.auth.login.AccountLockedException;

@Service
public class AuthServiceImpl implements AuthService {
    @Autowired
    private UserMapper userMapper;

    @Override
    public User login(UserLoginDTO userLoginDTO) {
        String username = userLoginDTO.getUsername();
        String password = userLoginDTO.getPassword();

        // 根据用户名查数据库中数据
        User user = userMapper.getByUsername(username);

        // 如果用户不存在
        if (user == null) {
            throw new RuntimeException();
        }

        // 如果密码错误
        // 对前端传过来的明文密码进行md5加密
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        if (!password.equals(user.getPassword())) {
            //密码错误
            throw new RuntimeException();
        }

        if (user.getStatus() == StatusConstant.DISABLE) {
            //账号被禁用
            throw new RuntimeException();
        }

        //3、返回实体对象
        return user;
    }
}
