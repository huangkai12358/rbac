package com.ymjrhk.rbac.exception;

/**
 * 密码错误异常
 */
public class AccountOrPasswordErrorException extends BaseException {

    public AccountOrPasswordErrorException() {
    }

    public AccountOrPasswordErrorException(String msg) {
        super(msg);
    }

}
