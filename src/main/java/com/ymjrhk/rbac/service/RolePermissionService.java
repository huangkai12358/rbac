package com.ymjrhk.rbac.service;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public interface RolePermissionService {
    void roleAssignPermissions(Long roleId, List<Long> permissionIds);
}
