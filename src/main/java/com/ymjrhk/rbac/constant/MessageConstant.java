package com.ymjrhk.rbac.constant;

/**
 * 信息提示常量类
 */
public class MessageConstant {
    // 40xxx 校验
    public static final String PARAMETER_FORMAT_ERROR = "参数格式错误"; // 40001
    public static final String FIELD_VALID_FAILED = "字段校验失败"; // 40002

    // 41xxx 登录
    public static final String ACCOUNT_OR_PASSWORD_ERROR = "账号或密码错误"; // 41001
    public static final String PASSWORD_ERROR = "密码错误"; // 41002
    public static final String ACCESS_DENIED = "无权限访问"; // 41003
    public static final String LOGIN_EXPIRED = "登录状态已失效，请重新登录"; // 42001

    // 42xxx 用户
    public static final String USER_NOT_LOGIN = "用户未登录"; // 42001
    public static final String USER_NOT_EXIST = "用户不存在"; // 42002
    public static final String USER_FORBIDDEN = "用户被禁用"; // 42003
    public static final String USER_CREATE_FAILED = "用户创建失败"; // 42004
    public static final String USERNAME_NOT_EXIST = "用户名不存在"; // 42005

    // 43xxx 角色
    public static final String ROLE_NOT_EXIST = "角色不存在"; // 43002
    public static final String ROLE_FORBIDDEN = "角色被禁用"; // 43003
    public static final String ROLE_CREATE_FAILED = "角色创建失败"; // 43004
    public static final String ROLE_NOT_EXIST_OR_DISABLED = "权限不存在或被禁用"; // 43005

    // 44xxx 权限
    public static final String PERMISSION_NOT_EXIST = "权限不存在"; // 44002
    public static final String PERMISSION_FORBIDDEN = "权限被禁用"; // 44003
    public static final String PERMISSION_CREATE_FAILED = "权限创建失败"; // 44004
    public static final String PERMISSION_NOT_EXIST_OR_DISABLED = "权限不存在或被禁用"; // 44005

    // 45xxx 分配
    public static final String ASSIGNMENT_ROLE_FAILED = "分配角色失败"; // 45001
    public static final String ASSIGNMENT_PERMISSION_FAILED = "分配权限失败"; // 45002
    public static final String ASSIGNMENT_NOT_OWNED_ROLE = "不能分配自己未拥有的角色"; // 45003
    public static final String ASSIGNMENT_NOT_OWNED_PERMISSION = "不能分配自己未拥有的权限"; // 45004
    public static final String ASSIGNMENT_PERMISSION_TO_SUPER_ADMIN = "不能为超级管理员角色分配权限";

    // 46xxx 业务通用
    public static final String STATUS_NOT_CHANGE = "状态未改变，无需修改"; // 46001
    public static final String UPDATE_FAILED = "更新失败，可能数据已被修改，请刷新后重试"; // 46002
    public static final String HISTORY_INSERT_FAILED = "写入历史失败"; // 46003

    // 47xxx 其他
    public static final String ALREADY_EXISTED = "已存在"; // 47001
    public static final String UPLOAD_FAILED = "文件上传失败";
    public static final String UNKNOWN_ERROR = "未知错误";

    public static final String ERROR_MESSAGE = "ERROR_MESSAGE";
}
