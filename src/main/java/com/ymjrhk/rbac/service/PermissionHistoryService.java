package com.ymjrhk.rbac.service;

public interface PermissionHistoryService {
    void recordHistory(Long permissionId, Integer operateType);
}
