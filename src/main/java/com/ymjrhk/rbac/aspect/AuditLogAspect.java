package com.ymjrhk.rbac.aspect;

import com.alibaba.fastjson2.JSON;
import com.ymjrhk.rbac.annotation.Audit;
import com.ymjrhk.rbac.context.UserContext;
import com.ymjrhk.rbac.entity.AuditLog;
import com.ymjrhk.rbac.service.AuditLogService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;

import static com.ymjrhk.rbac.utils.IpUtil.getClientIp;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class AuditLogAspect {

    private final AuditLogService auditLogService;

    @Around("@annotation(audit)")
    public Object around(ProceedingJoinPoint joinPoint, Audit audit) throws Throwable {
        log.info("AOP 审计日志表开始接管...");

        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        // 定时任务、非 HTTP 场景、单元测试可能会 NPE
        if (attributes == null) {
            return joinPoint.proceed();
        }

        HttpServletRequest request = attributes.getRequest();

        AuditLog auditLog = new AuditLog();

        String path = request.getRequestURI();
        String method = request.getMethod();

        auditLog.setPath(path);
        auditLog.setMethod(method);

        // 获得客户端 ip
        String ip = getClientIp(request);
        auditLog.setIp(ip);

        auditLog.setPermissionName(audit.permission());

        // 当前登录用户
        auditLog.setUserId(UserContext.getCurrentUserId());
        auditLog.setUsername(UserContext.getCurrentUsername());

        // 请求参数
        // 排除 HttpServletRequest、HttpServletResponse、MultipartFile
        Object[] args = Arrays.stream(joinPoint.getArgs())
                              .filter(arg ->
                                      !(arg instanceof HttpServletRequest) &&
                                              !(arg instanceof HttpServletResponse) &&
                                              !(arg instanceof MultipartFile)
                              )
                              .toArray();

        auditLog.setRequestBody(serializeArgsSafely(args));

        try {
            log.debug("尝试运行 Controller 方法...");
            Object result = joinPoint.proceed();
            auditLog.setSuccess(1);

            // GET + 成功 → 不记录
            if (!"GET".equalsIgnoreCase(method)) {
                log.info("接口运行无异常且为创建或更新，正常记录审计日志...");
                auditLogService.save(auditLog);
            }

            return result;
        } catch (Throwable t) { // Error（如 OOM）也会被记录
            auditLog.setSuccess(0);
            auditLog.setErrorMessage(t.getMessage());

            log.warn("接口出现异常，将记录到审计日志...");
            auditLogService.save(auditLog);

            throw t;
        } finally {
            log.info("AOP 审计日志表结束接管...");
        }
    }

    /**
     * 定义敏感字段名单
     */
    private static final String[] SENSITIVE_FIELDS = {
            "password",
            "oldPassword",
            "newPassword",
            "confirmPassword"
    };

    /**
     * 隐藏掉密码字段
     *
     * @param args
     * @return
     */
    private String serializeArgsSafely(Object[] args) {
        try {
            String json = JSON.toJSONString(args);
            for (String field : SENSITIVE_FIELDS) {
                json = json.replaceAll(
                        "(\"" + field + "\"\\s*:\\s*\")[^\"]*\"",
                        "$1******\""
                );
            }
            return json;
        } catch (Exception e) {
            return "[unserializable]";
        }
    }

}
