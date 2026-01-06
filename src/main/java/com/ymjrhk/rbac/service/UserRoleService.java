package com.ymjrhk.rbac.service;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public interface UserRoleService {
    void userAssignRoles(Long userId, List<Long> roleIds);
}
