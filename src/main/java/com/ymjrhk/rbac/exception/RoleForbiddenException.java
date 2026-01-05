package com.ymjrhk.rbac.exception;

/**
 * 用户被禁用异常
 */
public class RoleForbiddenException extends BaseException {

    public RoleForbiddenException() {
    }

    public RoleForbiddenException(String msg) {
        super(msg);
    }

}
