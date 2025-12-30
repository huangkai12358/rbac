package com.ymjrhk.rbac.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.ymjrhk.rbac.dto.UserLoginDTO;
import com.ymjrhk.rbac.entity.User;
import com.ymjrhk.rbac.mapper.UserMapper;
import com.ymjrhk.rbac.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;


    @Override
    public void create(UserLoginDTO userLoginDTO) {
        String nickname = userLoginDTO.getUsername();
        User user = BeanUtil.copyProperties(userLoginDTO, User.class);
        user.setNickname(nickname).
                setSecretToken("UUID");
        userMapper.create(user);

    }
}
