package com.ymjrhk.rbac.exception;

public class RoleNotExistOrDisabledException extends BaseException {

    public RoleNotExistOrDisabledException() {
    }

    public RoleNotExistOrDisabledException(String message) {
        super(message);
    }
}
