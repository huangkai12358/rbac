package com.ymjrhk.rbac.service;

public interface UserHistoryService {
    void recordHistory(Long userId, Integer operateType);
}
