package com.ymjrhk.rbac.interceptor;

import com.ymjrhk.rbac.constant.SuccessConstant;
import com.ymjrhk.rbac.context.UserContext;
import com.ymjrhk.rbac.entity.AuditLog;
import com.ymjrhk.rbac.service.AuditLogService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.nio.charset.StandardCharsets;

import static com.ymjrhk.rbac.constant.MessageConstant.ERROR_MESSAGE;
import static com.ymjrhk.rbac.utils.IpUtil.getClientIp;

// AOP：记录「进入 Controller 的业务执行审计」
// 缺失：请求在 Controller 之前失败的审计
// - JSON 格式错误
// - @Valid 校验失败
// - 参数类型错误
// - 缺少必填字段
@Component
@Slf4j
@RequiredArgsConstructor
public class AuditFailInterceptor implements HandlerInterceptor {

    private final AuditLogService auditLogService;

    @Override
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response,
                                Object handler,
                                Exception ex) {
        // 1. BAD_REQUEST(400) 不经过 AOP，在这里捕获
        int status = response.getStatus();
        if (status != HttpStatus.BAD_REQUEST.value()) {
            return;
        }

        log.info("拦截器-3 AuditFailInterceptor 开始运行...");

        try {
            AuditLog auditLog = new AuditLog();

            auditLog.setPath(request.getRequestURI());
            auditLog.setMethod(request.getMethod());
            auditLog.setIp(getClientIp(request));
            auditLog.setSuccess(SuccessConstant.FAIL);
            auditLog.setErrorMessage((String) request.getAttribute(ERROR_MESSAGE));

            // 当前用户（可能为空，没登录）
            auditLog.setUserId(UserContext.getCurrentUserId());
            auditLog.setUsername(UserContext.getCurrentUsername());

            // 请求体（从 ContentCachingRequestWrapper 里拿）
            if (request instanceof ContentCachingRequestWrapper wrapper) {
                byte[] body = wrapper.getContentAsByteArray();
                String rawBody = new String(body, StandardCharsets.UTF_8);
                String maskedBody = maskPasswordByRegex(rawBody);
                auditLog.setRequestBody(maskedBody);
            }

            auditLogService.save(auditLog);

            log.info("记录类型为“参数解析失败（请求参数格式错误 PARAMETER_FORMAT_ERROR）或校验失败（@Valid 失败）”的失败请求审计日志：{}", auditLog.getPath());

        } catch (Exception e) {
            // 审计失败不能影响主流程
            log.warn("记录参数类型的失败请求审计日志失败", e);
        }
    }

    public static String maskPasswordByRegex(String requestBody) {
        if (requestBody == null || requestBody.isBlank()) {
            return requestBody;
        }

        return requestBody.replaceAll(
                "(?i)(\"[^\"]*password[^\"]*\"\\s*:\\s*)\"[^\"]*\"",
                "$1\"******\""
        );

    }

}
