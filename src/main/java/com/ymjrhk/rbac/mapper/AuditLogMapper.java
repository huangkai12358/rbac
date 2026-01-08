package com.ymjrhk.rbac.mapper;

import com.github.pagehelper.Page;
import com.ymjrhk.rbac.dto.AuditLogRealPageQueryDTO;
import com.ymjrhk.rbac.vo.AuditLogVO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AuditLogMapper {
    /**
     * 审计日志分页查询
     * @param auditLogRealPageQueryDTO
     * @return
     */
    Page<AuditLogVO> pageQuery(AuditLogRealPageQueryDTO auditLogRealPageQueryDTO);
}
