package com.ymjrhk.rbac.service;

import com.ymjrhk.rbac.vo.RoleVO;

import java.util.List;

public interface UserRoleService {
    void assignRolesToUser(Long userId, List<Long> roleIds);

    List<RoleVO> getUserRoles(Long userId);

    boolean userHasRole(Long userId, String roleName);
}
