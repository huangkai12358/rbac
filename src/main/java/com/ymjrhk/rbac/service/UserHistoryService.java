package com.ymjrhk.rbac.service;

public interface UserHistoryService {
    void record(Long userId, Integer operateType);
}
