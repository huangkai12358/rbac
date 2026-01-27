package com.ymjrhk.rbac.exception;

/**
 * 用户被禁用异常
 */
public class RoleForbiddenException extends BaseException {

    public RoleForbiddenException(String msg) {
        super(msg);
    }

}
