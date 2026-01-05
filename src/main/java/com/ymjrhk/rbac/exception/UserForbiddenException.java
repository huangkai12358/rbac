package com.ymjrhk.rbac.exception;

/**
 * 用户被禁用异常
 */
public class UserForbiddenException extends BaseException {

    public UserForbiddenException() {
    }

    public UserForbiddenException(String msg) {
        super(msg);
    }

}
