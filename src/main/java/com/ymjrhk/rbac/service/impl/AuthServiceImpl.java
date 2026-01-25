package com.ymjrhk.rbac.service.impl;

import com.ymjrhk.rbac.constant.JwtClaimsConstant;
import com.ymjrhk.rbac.constant.MessageConstant;
import com.ymjrhk.rbac.constant.StatusConstant;
import com.ymjrhk.rbac.context.UserContext;
import com.ymjrhk.rbac.dto.UserLoginDTO;
import com.ymjrhk.rbac.entity.User;
import com.ymjrhk.rbac.exception.AccountOrPasswordErrorException;
import com.ymjrhk.rbac.exception.UserForbiddenException;
import com.ymjrhk.rbac.exception.UserNotLoginException;
import com.ymjrhk.rbac.mapper.UserMapper;
import com.ymjrhk.rbac.properties.JwtProperties;
import com.ymjrhk.rbac.service.AuthService;
import com.ymjrhk.rbac.service.UserService;
import com.ymjrhk.rbac.utils.JwtUtil;
import com.ymjrhk.rbac.vo.UserLoginVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {
    private final UserMapper userMapper;

    private final PasswordEncoder passwordEncoder;

    private final JwtProperties jwtProperties;

    private final UserService userService;

    /**
     * 用户登录
     *
     * @param userLoginDTO
     * @return
     */
    @Override
    @Transactional
    public UserLoginVO login(UserLoginDTO userLoginDTO) {
        String username = userLoginDTO.getUsername();
        String password = userLoginDTO.getPassword();

        // 1. 根据用户名查数据库中数据
        User user = userMapper.getByUsername(username);

        // 2. 处理各种异常情况（用户不存在、密码错误、账号被禁用）
        // 2.1 如果用户不存在（都统一返回账号或密码错误）
        if (user == null) {
            throw new AccountOrPasswordErrorException(MessageConstant.ACCOUNT_OR_PASSWORD_ERROR);
        }

        // 2.2 如果密码错误（都统一返回账号或密码错误）
        String peppered = password + "#" + user.getUserId(); // 加 userId 作为 pepper
        if (!passwordEncoder.matches(peppered, user.getPassword())) {
            throw new AccountOrPasswordErrorException(MessageConstant.ACCOUNT_OR_PASSWORD_ERROR);
        }

        // 2.3 如果账号被禁用
        if (Objects.equals(user.getStatus(), StatusConstant.DISABLED)) {
            throw new UserForbiddenException(MessageConstant.USER_FORBIDDEN);
        }

        // 3. 登录成功后，生成 JWT
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.USER_ID, user.getUserId()); // JWT 存 userId、username 和 authVersion
        claims.put(JwtClaimsConstant.USERNAME, user.getUsername());
        claims.put(JwtClaimsConstant.AUTH_VERSION, user.getAuthVersion());
        String token = JwtUtil.createJWT(
                jwtProperties.getSecretKey(),
                jwtProperties.getTtl(),
                claims);

        // 4. 返回 UserLoginVO
        return UserLoginVO.builder()
                          .userId(user.getUserId())
                          .username(user.getUsername())
                          .nickname(user.getNickname())
                          .token(token) // JWT 放到 token 中
                          .build();
    }

    /**
     * 用户登出
     */
    @Override
    @Transactional
    public void logout() {
        Long userId = UserContext.getCurrentUserId();
        if (userId == null) {
            // 理论上不会发生，兜底
            throw new UserNotLoginException(MessageConstant.USER_NOT_LOGIN);
        }

        // authVersion + 1
        // 不涉及其他字段修改，所以暂不考虑并发乐观锁字段，不用 update() 函数
        userService.incrementAuthVersion(userId);

        // TODO：写历史表
    }
}
