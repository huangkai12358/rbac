package com.ymjrhk.rbac.service.impl;

import com.ymjrhk.rbac.constant.StatusConstant;
import com.ymjrhk.rbac.context.LoginUser;
import com.ymjrhk.rbac.context.UserContext;
import com.ymjrhk.rbac.dto.UserLoginDTO;
import com.ymjrhk.rbac.entity.User;
import com.ymjrhk.rbac.exception.AccountOrPasswordErrorException;
import com.ymjrhk.rbac.exception.UserForbiddenException;
import com.ymjrhk.rbac.exception.UserNotLoginException;
import com.ymjrhk.rbac.mapper.UserMapper;
import com.ymjrhk.rbac.properties.JwtProperties;
import com.ymjrhk.rbac.service.UserService;
import com.ymjrhk.rbac.vo.UserLoginVO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @InjectMocks
    private AuthServiceImpl authService;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtProperties jwtProperties;

    // login() 中没用到，但构造器需要
    @Mock
    private UserService userService;

    @AfterEach
    void tearDown() {
        UserContext.clear(); // 清理 ThreadLocal，防止测试互相污染
    }

    // ========================= login() =========================

    /**
     * 登录成功
     */
    @Test
    void login_success() {
        // given
        UserLoginDTO dto = new UserLoginDTO();
        dto.setUsername("zhangsan");
        dto.setPassword("123456");

        User user = new User();
        user.setUserId(1L);
        user.setUsername("zhangsan");
        user.setNickname("张三");
        user.setPassword("ENCODED");
        user.setStatus(StatusConstant.ENABLED);
        user.setAuthVersion(2);

        when(userMapper.getByUsername("zhangsan"))
                .thenReturn(user);
        when(passwordEncoder.matches("123456#1", "ENCODED"))
                .thenReturn(true);
        when(jwtProperties.getSecretKey())
                .thenReturn("test-jwt-secret-key-32-bytes!!!!");
        when(jwtProperties.getTtl())
                .thenReturn(3600000L);

        // when
        UserLoginVO result = authService.login(dto);

        // then
        assertNotNull(result);
        assertEquals(1L, result.getUserId());
        assertEquals("zhangsan", result.getUsername());
        assertEquals("张三", result.getNickname());
        assertNotNull(result.getToken());

        verify(userMapper).getByUsername("zhangsan");
        verify(passwordEncoder)
                .matches("123456#1", "ENCODED");
    }

    /**
     * 用户不存在
     */
    @Test
    void login_userNotExist_throwException() {
        // given
        UserLoginDTO dto = new UserLoginDTO();
        dto.setUsername("nobody");
        dto.setPassword("123456");

        when(userMapper.getByUsername("nobody"))
                .thenReturn(null);

        // then
        assertThrows(AccountOrPasswordErrorException.class,
                () -> authService.login(dto));

        verify(userMapper).getByUsername("nobody");
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    /**
     * 密码错误
     */
    @Test
    void login_passwordError_throwException() {
        // given
        UserLoginDTO dto = new UserLoginDTO();
        dto.setUsername("zhangsan");
        dto.setPassword("wrong");

        User user = new User();
        user.setUserId(1L);
        user.setUsername("zhangsan");
        user.setPassword("ENCODED");
        user.setStatus(StatusConstant.ENABLED);

        when(userMapper.getByUsername("zhangsan"))
                .thenReturn(user);
        when(passwordEncoder.matches("wrong#1", "ENCODED"))
                .thenReturn(false);

        // then
        assertThrows(AccountOrPasswordErrorException.class,
                () -> authService.login(dto));

        verify(passwordEncoder)
                .matches("wrong#1", "ENCODED");
    }

    /**
     * 用户被禁用
     */
    @Test
    void login_userDisabled_throwException() {
        // given
        UserLoginDTO dto = new UserLoginDTO();
        dto.setUsername("zhangsan");
        dto.setPassword("123456");

        User user = new User();
        user.setUserId(1L);
        user.setUsername("zhangsan");
        user.setPassword("ENCODED");
        user.setStatus(StatusConstant.DISABLED);

        when(userMapper.getByUsername("zhangsan"))
                .thenReturn(user);
        when(passwordEncoder.matches("123456#1", "ENCODED"))
                .thenReturn(true);

        // then
        assertThrows(UserForbiddenException.class,
                () -> authService.login(dto));
    }

    // ========================= logout() =========================

    /**
     * 正常 logout
     */
    @Test
    void logout_success_incrementAuthVersion() {
        // given
        Long userId = 1L;
        UserContext.set(new LoginUser(userId, "zhangsan"));

        // when
        authService.logout();

        // then
        verify(userService).incrementAuthVersion(userId);
    }

    /**
     * 未登录直接 logout
     */
    @Test
    void logout_userNotLogin_throwException() {
        // given
        // 不设置 UserContext（默认就是 null）

        // then
        assertThrows(UserNotLoginException.class,
                () -> authService.logout());

        verify(userService, never()).incrementAuthVersion(anyLong());
    }







}

