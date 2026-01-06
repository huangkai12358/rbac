package com.ymjrhk.rbac.service;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public interface RolePermissionService {
    void userAssignRoles(Long userId, @NotEmpty(message = "id 列表不能为空") List<Long> roleIds);
}
