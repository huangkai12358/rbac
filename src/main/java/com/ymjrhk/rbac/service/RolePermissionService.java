package com.ymjrhk.rbac.service;

import com.ymjrhk.rbac.vo.RolePermissionVO;

import java.util.List;

public interface RolePermissionService {
    void assignPermissionsToRole(Long roleId, List<Long> permissionIds);

    List<RolePermissionVO> getRolePermissions(Long roleId);
}
