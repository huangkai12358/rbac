package com.ymjrhk.rbac;

import com.ymjrhk.rbac.controller.AuthController;
import com.ymjrhk.rbac.dto.UserLoginDTO;
import com.ymjrhk.rbac.entity.User;
import com.ymjrhk.rbac.result.Result;
import com.ymjrhk.rbac.service.AuthService;
import com.ymjrhk.rbac.vo.UserLoginVO;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class AuthServiceTest {
    @Autowired
    private AuthService authService;

    /**
     * 登录成功测试
     */
    @Test
    void login_success() {
        UserLoginDTO dto = new UserLoginDTO();
        dto.setUsername("admin");
        dto.setPassword("123456");

        UserLoginVO userLoginVO = authService.login(dto);

        assertNotNull(userLoginVO);
    }
}
