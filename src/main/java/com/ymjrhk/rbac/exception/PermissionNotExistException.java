package com.ymjrhk.rbac.exception;

public class PermissionNotExistException extends BaseException {

    public PermissionNotExistException() {
    }

    public PermissionNotExistException(String message) {
        super(message);
    }
}
