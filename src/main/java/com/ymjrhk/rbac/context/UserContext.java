package com.ymjrhk.rbac.context;

// 并发问题成立，必须同时满足两点：
// 1. 可变
// 2. 被多个线程共享

// | 对象             | 是否共享    | 是否可变  | 是否危险   |
// | -------------- | ------- | ----- | ------ |
// | LoginUser 实例   | ❌ 不共享   | ✔ 可变  | **安全** |
// | ThreadLocal.TL | ✔ 共享    | ❌（容器） | 安全     |
// | TL 内的值         | ❌ 按线程隔离 | ✔ 可变  | 安全     |

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
