package com.ymjrhk.rbac.exception;

public class RoleNotExistException extends BaseException {

    public RoleNotExistException() {
    }

    public RoleNotExistException(String message) {
        super(message);
    }
}
