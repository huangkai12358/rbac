package com.ymjrhk.rbac.constant;

/**
 * Spring Cache 缓存名统一定义
 *
 * 命名规范：
 * - 模块:语义
 * - 使用冒号分隔
 */
public final class CacheConstant {

    private CacheConstant() {}

    /* ================= 用户相关 ================= */

    /** 用户基础信息 */
    public static final String USER_BASIC = "user:basic";

    /** 用户鉴权信息 */
    public static final String USER_AUTH = "user:auth";

    /** 个人信息聚合视图（包括角色和权限） */
    public static final String USER_ME = "user:me";

    /** 用户 -> 权限列表 */
    public static final String USER_PERMISSIONS = "user:permissions";

    /** 用户 -> 角色列表 */
    public static final String USER_ROLES = "user:roles";

    /** 角色 -> 权限列表 */
    public static final String ROLE_PERMISSIONS = "role:permissions";

    /** 角色基础信息 */
    public static final String ROLE_BASIC = "role:basic";

    /** 权限基础信息 */
    public static final String PERMISSION_BASIC = "permission:basic";

    // 如果后面有「权限树 / 权限列表」缓存，可以预留
    public static final String PERMISSION_TREE = "permission:tree";

}
