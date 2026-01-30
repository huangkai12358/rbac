package com.ymjrhk.rbac.aspect;

import com.alibaba.fastjson2.JSON;
import com.ymjrhk.rbac.annotation.Audit;
import com.ymjrhk.rbac.constant.SuccessConstant;
import com.ymjrhk.rbac.context.UserContext;
import com.ymjrhk.rbac.entity.AuditLog;
import com.ymjrhk.rbac.service.AuditLogService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

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

//        // 排除 HttpServletRequest、HttpServletResponse、MultipartFile
//        // 暂时不用了，因为改成了不是获得全部参数（包括路径参数、Servlet 等），而是只获得 @ResponseBody 标注的请求体
//        Object[] args = Arrays.stream(joinPoint.getArgs())
//                              .filter(arg ->
//                                      !(arg instanceof HttpServletRequest) &&
//                                              !(arg instanceof HttpServletResponse) &&
//                                              !(arg instanceof MultipartFile)
//                              )
//                              .toArray();
//        auditLog.setRequestBody(serializeArgsSafely(args));

        // 请求参数
        Object body = extractRequestBody(joinPoint);
        auditLog.setRequestBody(serializeBodySafely(body));

        try {
            log.debug("尝试运行 Controller 方法...");
            Object result = joinPoint.proceed();
            auditLog.setSuccess(SuccessConstant.SUCCESS);

            // GET + 成功 → 不记录
            if (!"GET".equalsIgnoreCase(method)) {
                log.info("接口运行无异常且为创建或更新，正常记录审计日志...");
                auditLogService.save(auditLog);
            }

            return result;
        } catch (Throwable t) { // Error（如 OOM）也会被记录
            auditLog.setSuccess(SuccessConstant.FAIL);
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
    private static final Set<String> SENSITIVE_FIELDS = Set.of(
            "password",
            "oldPassword",
            "newPassword",
            "confirmPassword",
            "token",
            "accessToken",
            "refreshToken"
    );

    /**
     * 从 Controller 方法参数中提取被 @RequestBody 标注的参数。（提取请求体）
     *
     * <p>设计说明：
     * <ul>
     *   <li>仅提取 @RequestBody 参数，路径参数 / 请求参数已由 URI 表达</li>
     *   <li>天然排除 HttpServletRequest / HttpServletResponse / MultipartFile</li>
     *   <li>支持多个 @RequestBody（极少见场景）</li>
     * </ul>
     *
     * @return <ul>
     *   <li>无 @RequestBody 时返回 null</li>
     *   <li>只有一个 @RequestBody 时返回该对象</li>
     *   <li>多个 @RequestBody 时返回 List</li>
     * </ul>
     */
    private Object extractRequestBody(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        Parameter[] parameters = method.getParameters();
        Object[] args = joinPoint.getArgs();

        List<Object> bodies = new ArrayList<>();

        for (int i = 0; i < parameters.length; i++) {
            if (parameters[i].isAnnotationPresent(RequestBody.class)) {
                bodies.add(args[i]);
            }
        }

        if (bodies.isEmpty()) {
            return null;
        }

        // 绝大多数 Controller 只有一个 @RequestBody
        return bodies.size() == 1 ? bodies.getFirst() : bodies;
    }

    /**
     * 将请求体对象安全序列化为 JSON 字符串。（把请求体中敏感字段安全地脱敏）
     *
     * <p>处理流程：
     * <ol>
     *   <li>先对对象进行递归脱敏</li>
     *   <li>再将脱敏后的对象序列化为 JSON</li>
     * </ol>
     *
     * <p>任何异常都不会影响业务流程，失败时返回占位字符串。
     */
    private String serializeBodySafely(Object body) {
        if (body == null) {
            return null;
        }

        try {
            Object sanitized = sanitizeObject(body);
            return JSON.toJSONString(sanitized);
        } catch (Exception e) {
            // 审计日志不应影响主业务流程
            log.warn("审计日志序列化失败", e);
            return "[unserializable]";
        }
    }

    /**
     * 对对象进行递归脱敏处理。（先遍历 Java 对象结构，把敏感字段的值替换掉，再把“干净对象”序列化成 JSON）
     *
     * <p>设计原则：
     * <ul>
     *   <li>脱敏发生在“对象层”，而不是 JSON 字符串层</li>
     *   <li>根据对象实际类型（基本类型 / Map / Collection / Java Bean）分别处理</li>
     *   <li>仅对命中的敏感字段替换值，其余字段递归处理</li>
     * </ul>
     *
     * <p>返回值始终是一个“可安全序列化”的对象结构（Map / List / 基本类型）。
     */
    private Object sanitizeObject(Object obj) {
        // null 直接返回，作为递归终止条件之一
        if (obj == null) {
            return null;
        }

        // 基本类型（叶子节点），无需脱敏也无需继续递归
        if (obj instanceof String || obj instanceof Number || obj instanceof Boolean) {
            return obj;
        }

        // 处理 Map 结构（常见于动态 JSON、反序列化对象）
        // 按 key 判断是否为敏感字段，对 value 递归脱敏
        if (obj instanceof Map<?, ?> map) {
            Map<String, Object> copy = new LinkedHashMap<>();
            map.forEach((k, v) -> {
                String key = String.valueOf(k);
                if (SENSITIVE_FIELDS.contains(key)) {
                    // 命中敏感字段，直接替换为脱敏值
                    copy.put(key, "******");
                } else {
                    // 非敏感字段，递归处理其 value
                    copy.put(key, sanitizeObject(v));
                }
            });
            return copy;
        }

        // 处理集合类型（List / Set 等）
        // 对集合中的每个元素递归脱敏
        if (obj instanceof Collection<?> col) {
            return col.stream()
                      .map(this::sanitizeObject)
                      .toList();
        }

        // 处理普通 Java Bean（DTO / VO 等）
        // 使用反射读取字段并构造安全的 Map 结构
        Map<String, Object> result = new LinkedHashMap<>();
        for (Field field : obj.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            try {
                Object value = field.get(obj);
                if (SENSITIVE_FIELDS.contains(field.getName())) {
                    // 字段名命中敏感字段，直接脱敏
                    result.put(field.getName(), "******");
                } else {
                    // 非敏感字段，递归脱敏其值
                    result.put(field.getName(), sanitizeObject(value));
                }
            } catch (IllegalAccessException ignored) {
                // 反射失败时忽略该字段，避免影响整体日志记录
            }
        }
        return result;
    }

    /**
     * 隐藏掉密码字段（暂时不用这个方法了）
     *
     * @param args
     * @return
     */
    private String serializeArgsSafely(List<Object> args) {
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
