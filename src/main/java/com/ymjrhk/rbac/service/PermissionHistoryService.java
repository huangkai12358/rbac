package com.ymjrhk.rbac.service;

public interface PermissionHistoryService {
    void record(Long permissionId, Integer operateType);
}
