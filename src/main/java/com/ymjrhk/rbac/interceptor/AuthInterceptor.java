package com.ymjrhk.rbac.interceptor;

import com.ymjrhk.rbac.constant.JwtClaimsConstant;
import com.ymjrhk.rbac.constant.StatusConstant;
import com.ymjrhk.rbac.context.LoginUser;
import com.ymjrhk.rbac.context.UserContext;
import com.ymjrhk.rbac.dto.auth.UserAuthInfo;
import com.ymjrhk.rbac.exception.UserNotLoginException;
import com.ymjrhk.rbac.properties.JwtProperties;
import com.ymjrhk.rbac.service.UserService;
import com.ymjrhk.rbac.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import static com.ymjrhk.rbac.constant.MessageConstant.*;

/**
 * 认证拦截器
 * 职责：
 * 1. 校验 JWT 是否存在、是否合法
 * 2. 校验用户是否存在、是否被禁用、用户名是否一致（其实用户名是否一致无需检测，若变化则 authVersion 必定变化）
 * 3. 校验 authVersion（强制失效）
 * 4. 设置 UserContext
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AuthInterceptor implements HandlerInterceptor {

    private final JwtProperties jwtProperties;

    private final UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) {

        log.info("拦截器-1 AuthInterceptor 开始运行...");

        // 1. 非 Controller 请求直接放行
        // 判断当前拦截到的是 Controller 的方法还是其他资源
        if (!(handler instanceof HandlerMethod)) {
            return true; //当前拦截到的不是动态方法，直接放行
        }

        // 2. 获取 Token
        String authHeader = request.getHeader(jwtProperties.getHeader());

        log.info("authHeader={}", authHeader);

        if (authHeader == null || authHeader.isBlank()) {
            throw new UserNotLoginException(USER_NOT_LOGIN);
        }
        if (!authHeader.startsWith(jwtProperties.getPrefix())) {
            throw new UserNotLoginException(USER_NOT_LOGIN);
        }

        // 截取真正的 JWT
        String token = authHeader.substring(jwtProperties.getPrefix().length()); // "Bearer ".length() = 7

        if (token.isBlank()) {
            throw new UserNotLoginException(USER_NOT_LOGIN);
        }

        try {
            // 3. 校验 Token（认证）
            log.info("JWT 校验：{}", token);
            Claims claims = JwtUtil.parseJWT(jwtProperties.getSecretKey(), token);

            Long userId = Long.valueOf(claims.get(JwtClaimsConstant.USER_ID).toString());
            String username = claims.get(JwtClaimsConstant.USERNAME).toString();
            Integer authVersion =
                    Integer.valueOf(claims.get(JwtClaimsConstant.AUTH_VERSION).toString());

            log.info("当前 userId：{}，username：{}，authVersion：{}", userId, username, authVersion);

            // 4. 查询数据库中用户最小认证信息

            UserAuthInfo authInfo = userService.getUserAuthInfo(userId);

            // 4.1 校验用户
            if (authInfo == null) {
                log.warn("JWT 校验失败：用户不存在 userId={}", userId);
                throw new UserNotLoginException(USER_NOT_LOGIN);
            }

            // 4.2 校验用户状态
            if (authInfo.getStatus() == null || authInfo.getStatus() == StatusConstant.DISABLE) {
                log.warn("JWT 校验失败：用户被禁用 userId={}", userId);
                throw new UserNotLoginException(USER_FORBIDDEN);
            }

            // 4.3 校验用户名
            if (authInfo.getUsername() == null || !authInfo.getUsername().equals(username)) {
                log.warn("JWT 校验失败：JWT 中用户名与数据库不一致 username={} dbUsername={} userId={}",
                        username,
                        authInfo.getUsername(),
                        userId);
                throw new UserNotLoginException(USERNAME_NOT_EXIST);
            }

            // 4.4 校验 authVersion（强制失效）
            if (!authVersion.equals(authInfo.getAuthVersion())) {
                log.warn("JWT 已失效：tokenVersion={}, dbVersion={}, userId={}",
                        authVersion,
                        authInfo.getAuthVersion(),
                        userId);
                throw new UserNotLoginException(LOGIN_EXPIRED);
            }

            // 5. 保存上下文，调用ThreadLocale
            UserContext.set(new LoginUser(userId, username));

            return true;

        } catch (UserNotLoginException e) {
            throw e;
        } catch (Exception e) {
            log.warn("JWT 校验异常", e);
            throw new UserNotLoginException(USER_NOT_LOGIN);
        }
//            // 不通过，响应 401 状态码
//            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//            response.setStatus(401);
//            return false;
    }

    @Override
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response,
                                Object handler,
                                Exception ex) {
        // 移除用户，清理 ThreadLocal，防止线程复用污染
        UserContext.clear();
    }
}
