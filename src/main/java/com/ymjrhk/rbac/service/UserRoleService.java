package com.ymjrhk.rbac.service;

import com.ymjrhk.rbac.vo.UserRoleVO;

import java.util.List;

public interface UserRoleService {
    void assignRolesToUser(Long userId, List<Long> roleIds);

    List<UserRoleVO> getUserRoles(Long userId);
}
