package com.ymjrhk.rbac.exception;

/**
 * 用户被禁用异常
 */
public class PermissionForbiddenException extends BaseException {

    public PermissionForbiddenException() {
    }

    public PermissionForbiddenException(String msg) {
        super(msg);
    }

}
