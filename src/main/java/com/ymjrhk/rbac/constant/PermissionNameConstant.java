package com.ymjrhk.rbac.constant;

public class PermissionNameConstant {

    private PermissionNameConstant() {
        // 防止实例化
    }

    public static final String AUTH_LOGIN = "AUTH:LOGIN";

    public static final String ME_VIEW = "ME:VIEW";
    public static final String ME_UPDATE = "ME:UPDATE";
    public static final String ME_PASSWORD_CHANGE = "ME:PASSWORD:CHANGE";

    public static final String USER_VIEW = "USER:VIEW";
    public static final String USER_CREATE = "USER:CREATE";
    public static final String USER_UPDATE = "USER:UPDATE";
    public static final String USER_STATUS = "USER:STATUS";
    public static final String USER_PASSWORD_RESET = "USER:PASSWORD:RESET";
    public static final String USER_ASSIGN = "USER:ASSIGN";

    public static final String ROLE_VIEW = "ROLE:VIEW";
    public static final String ROLE_CREATE = "ROLE:CREATE";
    public static final String ROLE_UPDATE = "ROLE:UPDATE";
    public static final String ROLE_STATUS = "ROLE:STATUS";
    public static final String ROLE_ASSIGN = "ROLE:ASSIGN";

    public static final String PERMISSION_VIEW = "PERMISSION:VIEW";
    public static final String PERMISSION_CREATE = "PERMISSION:CREATE";
    public static final String PERMISSION_UPDATE = "PERMISSION:UPDATE";
    public static final String PERMISSION_STATUS = "PERMISSION:STATUS";

    public static final String AUDIT_VIEW = "AUDIT:VIEW";


}
