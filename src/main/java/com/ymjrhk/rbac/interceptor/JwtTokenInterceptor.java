package com.ymjrhk.rbac.interceptor;

import com.ymjrhk.rbac.constant.JwtClaimsConstant;
import com.ymjrhk.rbac.context.BaseContext;
import com.ymjrhk.rbac.exception.AccessDeniedException;
import com.ymjrhk.rbac.exception.UserNotLoginException;
import com.ymjrhk.rbac.properties.JwtProperties;
import com.ymjrhk.rbac.service.UserService;
import com.ymjrhk.rbac.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import static com.ymjrhk.rbac.constant.MessageConstant.AccessDenied;
import static com.ymjrhk.rbac.constant.MessageConstant.USER_NOT_LOGIN;

/**
 * jwt令牌校验的拦截器
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtTokenInterceptor implements HandlerInterceptor {

    private final JwtProperties jwtProperties;

    private final UserService userService;

    /**
     * 校验jwt
     *
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 1. 非 Controller 请求直接放行
        // 判断当前拦截到的是 Controller 的方法还是其他资源
        if (!(handler instanceof HandlerMethod)) {
            return true; //当前拦截到的不是动态方法，直接放行
        }

        // 2. 获取 Token
        String token = request.getHeader(jwtProperties.getTokenName());

        if (token == null || token.isEmpty()) {
            throw new UserNotLoginException(USER_NOT_LOGIN);
        }

        try {
            // 3. 校验 Token（认证）
            log.info("JWT 校验：{}", token);
            Claims claims = JwtUtil.parseJWT(jwtProperties.getSecretKey(), token);

            Long userId = Long.valueOf(claims.get(JwtClaimsConstant.USER_ID).toString());

            log.info("当前用户id：{}", userId);
            // 4. 保存上下文，调用ThreadLocale
            BaseContext.setCurrentUserId(userId);

            // 5. 鉴权（核心）
            log.info("当前请求 URI：{}，请求方法：{}", request.getRequestURI(), request.getMethod());
            boolean allowed = userService.hasPermission(userId, request.getRequestURI(), request.getMethod());

            if (!allowed) {
                throw new AccessDeniedException(AccessDenied);
            }

            return true;

        } catch (UserNotLoginException | AccessDeniedException e) {
            throw e;
        } catch (Exception e) {
            log.error("JWT 校验失败", e);
            throw new UserNotLoginException(USER_NOT_LOGIN);
        }

//            // 4、不通过，响应 401 状态码
//            response.setStatus(401);
//            return false;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 移除用户
        BaseContext.removeCurrentUserId();
    }
}
