package com.ymjrhk.rbac.result;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResultCode {
    SUCCESS(0, "success"),

    VALIDATION_ERROR(400, "参数错误"),
    UNAUTHORIZED(401, "未登录"),
    FORBIDDEN(403, "无权限"),
    NOT_FOUND(404, "资源不存在"),

    CONFLICT(409, "数据冲突"),

    SYSTEM_ERROR(500, "系统异常");

    private final int code;
    private final String message;
}