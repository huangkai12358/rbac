package com.ymjrhk.rbac;

import com.ymjrhk.rbac.entity.User;
import com.ymjrhk.rbac.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

@SpringBootTest
public class UserMapperTest {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * 测试用户名和密码创建用户
     */
    @Test
    void createSimpleUser() {
        User user = new User();
        user.setUsername("test7");
        user.setPassword(passwordEncoder.encode("123456"));
        user.setNickname("测试7");
        user.setSecretToken(UUID.randomUUID().toString());
        user.setCreateUserId(7L);
        user.setUpdateUserId(7L);

        userMapper.insert(user);
    }

    /**
     * 测试多字段创建用户
     */
    @Test
    void createComplexUser() {
        User user = new User();
        user.setUsername("test6");
        user.setPassword(passwordEncoder.encode("123456"));
        user.setNickname("测试6");
        user.setEmail("110@qq.com");
        user.setStatus(0);
        user.setSecretToken(UUID.randomUUID().toString());
        user.setCreateUserId(7L);
        user.setUpdateUserId(7L);

        userMapper.insert(user);
    }
}
