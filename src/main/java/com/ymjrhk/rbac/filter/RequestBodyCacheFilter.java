package com.ymjrhk.rbac.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.IOException;

/**
 * 请求体本质是 InputStream，只能读一次
 * 在Controller里读了，AuditFailInterceptor 的 getRequestBody 就读不到
 * 所以拦截器里不能直接读请求体，需要用 ContentCachingRequestWrapper 包装 request
 * <p>
 * Filter（最外层）
 * └── 用 ContentCachingRequestWrapper 包装 request
 * ↓
 * Interceptor
 * └── 从 wrapper 里“安全地”拿请求体
 * ↓
 * Controller
 * └── @RequestBody 正常工作
 * ↓
 * afterCompletion Interceptor - AuditFailInterceptor
 * └── 从 wrapper 里“安全地”拿请求体
 */
@Component
@Slf4j
public class RequestBodyCacheFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain chain)
            throws IOException, ServletException {

        log.info("过滤器开始运行...");

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String uri = httpRequest.getRequestURI();

        // ===== Swagger / Knife4j 直接放行 =====
        if (uri.startsWith("/v3/api-docs")
                || uri.startsWith("/doc.html")
                || uri.startsWith("/swagger-ui")
                || uri.startsWith("/webjars")
                || uri.contains("/webjars/")) {

            chain.doFilter(request, response);
            return;
        }

        ContentCachingRequestWrapper wrappedRequest =
                new ContentCachingRequestWrapper(httpRequest);

        chain.doFilter(wrappedRequest, response);
    }
}
