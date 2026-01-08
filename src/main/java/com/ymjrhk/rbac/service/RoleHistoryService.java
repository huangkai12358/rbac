package com.ymjrhk.rbac.service;

public interface RoleHistoryService {
    void record(Long roleId, Integer operateType);
}
