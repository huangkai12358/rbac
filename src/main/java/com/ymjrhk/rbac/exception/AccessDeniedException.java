package com.ymjrhk.rbac.exception;

/**
 * 无权限访问
 */
public class AccessDeniedException extends BaseException {

    public AccessDeniedException() {
    }

    public AccessDeniedException(String msg) {
        super(msg);
    }

}
