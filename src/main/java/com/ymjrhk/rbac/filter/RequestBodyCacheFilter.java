package com.ymjrhk.rbac.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.IOException;

/**
 * 请求体本质是 InputStream，只能读一次
 * 在拦截器里读了，Controller 的 @RequestBody 就会炸
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

        ContentCachingRequestWrapper wrappedRequest =
                new ContentCachingRequestWrapper(httpRequest);

        chain.doFilter(wrappedRequest, response);
    }
}
