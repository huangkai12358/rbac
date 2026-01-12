package com.ymjrhk.rbac.context;

public class UserContext {

    private static final ThreadLocal<LoginUser> TL = new ThreadLocal<>();

    public static void set(LoginUser user) {
        TL.set(user);
    }

    public static LoginUser get() {
        return TL.get();
    }

    public static Long getCurrentUserId() {
        return TL.get() == null ? null : TL.get().getUserId();
    }

    public static String getCurrentUsername() {
        return TL.get() == null ? null : TL.get().getUsername();
    }

    public static void clear() {
        TL.remove();
    }
}
