package com.ymjrhk.rbac.interceptor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ymjrhk.rbac.annotation.Audit;
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
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Map;

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
                String maskedBody = maskPasswordLinear(rawBody);
                auditLog.setRequestBody(maskedBody);
            }

            /*
            实现参数格式错误或校验失败时可以记录 permissionName：
            能拿到的前提：
            1. DispatcherServlet 已经成功匹配到 Controller 方法
            2. handler instanceof HandlerMethod 为 true
           ️3. 方法上有 @Audit(permission = ...)
            4. 参数错误发生在 参数绑定 / 校验阶段
            */
            if (handler instanceof HandlerMethod handlerMethod) {
                Audit audit = handlerMethod.getMethodAnnotation(Audit.class);
                if (audit != null) {
                    auditLog.setPermissionName(audit.permission());
                }
            }

            auditLogService.save(auditLog); // 异步执行

            log.info("记录类型为“参数解析失败（请求参数格式错误 PARAMETER_FORMAT_ERROR）或校验失败（@Valid 失败）”的失败请求审计日志：{}", auditLog.getPath());

        } catch (Exception e) {
            // 审计失败不能影响主流程（实际上由于 auditLogService.save(auditLog) 异步执行，所以即使发生异常也不会在这里被捕获）
            log.warn("记录参数类型的失败请求审计日志失败", e);
        }
    }

    /**
     * 含 password 字段脱敏方法一：用 JSON 解析器
     * 100% 确定是 JSON
     * <p>
     * - 尝试把 requestBody 当 JSON 解析
     * - 递归遍历对象/数组
     * - key 名包含 password（大小写不敏感）就把 value 替换成 "******"
     * - 解析失败就原样返回（或做降级）
     * <p>
     * 优点：彻底消灭 ReDoS；还能处理嵌套对象/数组；不会误伤字符串里的内容
     * 缺点：requestBody 不是 JSON 时无法脱敏 password 字段，需要降级；比正则多一点代码
     *
     * @param requestBody
     * @return
     */
    public static String maskPasswordSafely(String requestBody) {
        if (requestBody == null || requestBody.isBlank()) {
            return requestBody;
        }

        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(requestBody);
            maskPasswordNode(root);
            return mapper.writeValueAsString(root);
        } catch (Exception e) {
            // 不是合法 JSON，直接返回原始内容
            return requestBody;
        }
    }

    private static void maskPasswordNode(JsonNode node) {
        if (node.isObject()) {
            ObjectNode obj = (ObjectNode) node;
            Iterator<Map.Entry<String, JsonNode>> fields = obj.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> entry = fields.next();
                String key = entry.getKey();
                JsonNode value = entry.getValue();

                if (key.toLowerCase().contains("password") && value.isValueNode()) {
                    obj.put(key, "******");
                } else {
                    maskPasswordNode(value);
                }
            }
        } else if (node.isArray()) {
            for (JsonNode child : node) {
                maskPasswordNode(child);
            }
        }
    }

    /**
     * 含 password 字段脱敏方法二：用“线性扫描”
     * requestBody 可能不是合法 JSON，但仍要尽量脱敏
     * <p>
     * - 找到 "xxxpasswordxxx" 这种 key（大小写不敏感）
     * - 跳过空白、冒号
     * - 如果 value 是字符串 "..."，把内容替换为 "******"（保留引号）
     * - 其余类型（数字/对象/数组/null）可选择不处理或也替换
     * <p>
     * 优点：可以替换全部出现 password 的字段；线性时间 O(n)，不会被构造输入拖垮
     * 缺点：实现比 regex 多，但仍可控
     *
     * @param body
     * @return
     */
    public static String maskPasswordLinear(String body) {
        if (body == null || body.isBlank()) {
            return body;
        }

        StringBuilder result = new StringBuilder(body.length());
        int i = 0;
        int n = body.length();

        while (i < n) {
            char c = body.charAt(i);

            // 1. 找到 JSON key（以 " 开头）
            if (c == '"') {
                int keyStart = i + 1;
                int keyEnd = keyStart;

                // 找 key 的结束引号
                while (keyEnd < n && body.charAt(keyEnd) != '"') {
                    keyEnd++;
                }

                if (keyEnd >= n) {
                    // 非法 JSON，直接追加剩余内容
                    result.append(body.substring(i));
                    break;
                }

                String key = body.substring(keyStart, keyEnd);
                String keyLower = key.toLowerCase();

                result.append('"').append(key).append('"');
                i = keyEnd + 1;

                // 2. 跳过空白
                while (i < n && Character.isWhitespace(body.charAt(i))) {
                    result.append(body.charAt(i));
                    i++;
                }

                // 3. 判断是否是 key-value 结构
                if (i < n && body.charAt(i) == ':') {
                    result.append(':');
                    i++;

                    while (i < n && Character.isWhitespace(body.charAt(i))) {
                        result.append(body.charAt(i));
                        i++;
                    }

                    // 4. key 包含 password，且 value 是字符串
                    if (keyLower.contains("password")
                            && i < n
                            && body.charAt(i) == '"') {

                        // 跳过原始 value
                        i++; // 跳过 opening quote
                        while (i < n && body.charAt(i) != '"') {
                            i++;
                        }
                        if (i < n) {
                            i++; // 跳过 closing quote
                        }

                        // 写入脱敏值
                        result.append("\"******\"");
                        continue;
                    }
                }

                // 不是 password，正常继续
                continue;
            }

            // 普通字符直接拷贝
            result.append(c);
            i++;
        }

        return result.toString();
    }

    /**
     * 含 password 字段脱敏方法三：正则
     *
     * 优点：可以替换全部出现 password 的字段
     * 缺点：正则可能触发 ReDoS（正则拒绝服务）—— 某些输入会让回溯爆炸，CPU 飙满。
     * @param requestBody
     * @return
     */
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
