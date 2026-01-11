package com.ymjrhk.rbac.service;

import com.ymjrhk.rbac.vo.PermissionVO;

import java.util.List;

public interface RolePermissionService {
    void assignPermissionsToRole(Long roleId, List<Long> permissionIds);

    List<PermissionVO> getRolePermissions(Long roleId);
}
