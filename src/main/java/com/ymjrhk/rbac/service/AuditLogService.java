package com.ymjrhk.rbac.service;

import com.ymjrhk.rbac.dto.AuditLogPageQueryDTO;
import com.ymjrhk.rbac.entity.AuditLog;
import com.ymjrhk.rbac.result.PageResult;
import com.ymjrhk.rbac.vo.AuditLogExcelVO;

import java.util.List;

public interface AuditLogService {
    PageResult pageQuery(AuditLogPageQueryDTO auditPageQueryDTO);

    void save(AuditLog auditLog);

    void saveLoginLog(Long userId,
                      String username,
                      String requestBody,
                      String ip,
                      int success,
                      String errorMessage);

    void saveForbiddenLog(AuditLog auditLog);

    List<AuditLogExcelVO> listForExport(AuditLogPageQueryDTO queryDTO);
}
