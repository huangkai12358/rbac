package com.ymjrhk.rbac.controller;

import com.ymjrhk.rbac.dto.UserLoginDTO;
import com.ymjrhk.rbac.entity.User;
import com.ymjrhk.rbac.properties.JwtProperties;
import com.ymjrhk.rbac.result.Result;
import com.ymjrhk.rbac.service.AuthService;
import com.ymjrhk.rbac.utils.JwtUtil;
import com.ymjrhk.rbac.vo.UserLoginVO;
import com.ymjrhk.rbac.constant.JwtClaimsConstant;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@Slf4j
@Tag(name = "登录与登出")
public class AuthController {
    @Autowired
    private AuthService authService;

    @Autowired
    private JwtProperties jwtProperties;

    @PostMapping("/login")
    @Operation(summary = "用户登录")
    public Result login(@RequestBody UserLoginDTO userLoginDTO) {
        log.info("员工登录：{}", userLoginDTO);

        User user = authService.login(userLoginDTO);

        //登录成功后，生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.USER_ID, user.getUserId());
        claims.put(JwtClaimsConstant.AUTH_VERSION, user.getAuthVersion());
        String token = JwtUtil.createJWT(
                jwtProperties.getSecretKey(),
                jwtProperties.getTtl(),
                claims);

        UserLoginVO userLoginVO = UserLoginVO.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .token(token)
                .build();

        return Result.success(userLoginVO);
    }
}
