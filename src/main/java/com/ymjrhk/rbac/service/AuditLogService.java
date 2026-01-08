package com.ymjrhk.rbac.service;

import com.ymjrhk.rbac.dto.AuditLogPageQueryDTO;
import com.ymjrhk.rbac.result.PageResult;

public interface AuditLogService {
    PageResult pageQuery(AuditLogPageQueryDTO auditPageQueryDTO);
}
