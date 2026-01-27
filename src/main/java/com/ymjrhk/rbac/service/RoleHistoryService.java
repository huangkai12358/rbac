package com.ymjrhk.rbac.service;

public interface RoleHistoryService {
    void recordHistory(Long roleId, Integer operateType);
}
