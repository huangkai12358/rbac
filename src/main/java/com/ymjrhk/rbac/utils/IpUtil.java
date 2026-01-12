package com.ymjrhk.rbac.utils;

import jakarta.servlet.http.HttpServletRequest;

public class IpUtil {

    /**
     * 获取客户端 ip
     * @param request
     * @return
     */
    public static String getClientIp(HttpServletRequest request) {
        // 1. 优先从 X-Forwarded-For 取（最可能是 IPv4）
        String ip = request.getHeader("X-Forwarded-For");
        if (isValidIp(ip)) {
            return extractFirstIp(ip);
        }

        // 2. 再取 X-Real-IP
        ip = request.getHeader("X-Real-IP");
        if (isValidIp(ip)) {
            return ip;
        }

        // 3. 最后兜底 RemoteAddr
        ip = request.getRemoteAddr();

        // 4. IPv6 localhost → IPv4 localhost
        if ("::1".equals(ip) || "0:0:0:0:0:0:0:1".equals(ip)) {
            return "127.0.0.1";
        }

        return ip;
    }

    private static boolean isValidIp(String ip) {
        return ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip);
    }

    private static String extractFirstIp(String ip) {
        // X-Forwarded-For 可能是多个 IP
        return ip.split(",")[0].trim();
    }
}
