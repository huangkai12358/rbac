package com.ymjrhk.rbac.interceptor;

import com.ymjrhk.rbac.constant.SuccessConstant;
import com.ymjrhk.rbac.context.UserContext;
import com.ymjrhk.rbac.entity.AuditLog;
import com.ymjrhk.rbac.exception.AccessDeniedException;
import com.ymjrhk.rbac.service.AuditLogService;
import com.ymjrhk.rbac.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static com.ymjrhk.rbac.constant.MessageConstant.ACCESS_DENIED;
import static com.ymjrhk.rbac.utils.IpUtil.getClientIp;

/**
 * 鉴权拦截器，RBAC 权限校验
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PermissionInterceptor implements HandlerInterceptor {

    private final UserService userService;

    private final AuditLogService auditLogService;

    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    // 仅认证接口
    private static final List<String> AUTH_ONLY_PATHS = List.of(
            "/api/me/**"
    );

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) {

        log.info("拦截器-2 PermissionInterceptor 开始运行...");

        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        // 1. auth-only 接口跳过鉴权（拦截器配置层已挡掉，此处为冗余设计）
        if (isAuthOnlyPath(request)) {
            log.info("auth-only 接口，跳过权限校验");
            return true;
        }

        // 2. 鉴权（核心）
        log.info("开始鉴权...");
        log.info("当前请求 URI：{}，请求方法：{}", request.getRequestURI(), request.getMethod());

        Long userId = UserContext.getCurrentUserId();

        boolean allowed = userService.hasPermission(userId,
                request.getRequestURI(),
                request.getMethod());

        if (!allowed) {
            log.warn("未授权访问，将保存到审计日志表中...");

            AuditLog auditLog = new AuditLog();

            auditLog.setUserId(UserContext.getCurrentUserId());
            auditLog.setUsername(UserContext.getCurrentUsername());
            auditLog.setPath(request.getRequestURI());
            auditLog.setMethod(request.getMethod());
            auditLog.setRequestBody(getRequestBody(request));
            auditLog.setIp(getClientIp(request));
            auditLog.setSuccess(SuccessConstant.FAIL);
            auditLog.setErrorMessage(ACCESS_DENIED);

            auditLogService.saveForbiddenLog(auditLog);

            throw new AccessDeniedException(ACCESS_DENIED);
        }

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception
            ex) {
        // 移除用户，清理 ThreadLocal，防止线程复用污染
        UserContext.clear();
    }

    /**
     * 工具方法，在拦截器里安全获取请求体
     *
     * @param request
     * @return
     */
    private String getRequestBody(HttpServletRequest request) {

        if (request instanceof ContentCachingRequestWrapper wrapper) {
            byte[] body = wrapper.getContentAsByteArray();
            if (body.length == 0) {
                return null;
            }
            return new String(body, StandardCharsets.UTF_8);
        }
        return null;
    }

    /**
     * 是否是仅需要认证（Authentication）不需要鉴权（Authorization）的路径
     *
     * @param request
     * @return
     */
    private boolean isAuthOnlyPath(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return AUTH_ONLY_PATHS.stream()
                              .anyMatch(p -> PATH_MATCHER.match(p, uri));
    }
}
