package com.ymjrhk.rbac.exception;

public class PermissionNotExistOrDisabledException extends BaseException {

    public PermissionNotExistOrDisabledException() {
    }

    public PermissionNotExistOrDisabledException(String message) {
        super(message);
    }
}
