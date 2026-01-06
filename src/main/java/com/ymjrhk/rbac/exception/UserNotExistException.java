package com.ymjrhk.rbac.exception;

public class UserNotExistException extends BaseException {

    public UserNotExistException() {
    }

    public UserNotExistException(String message) {
        super(message);
    }
}
