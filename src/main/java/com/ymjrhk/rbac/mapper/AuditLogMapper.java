package com.ymjrhk.rbac.mapper;

import com.ymjrhk.rbac.dto.AuditLogRealPageQueryDTO;
import com.ymjrhk.rbac.entity.AuditLog;
import com.ymjrhk.rbac.vo.AuditLogVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AuditLogMapper {
    /**
     * 审计日志分页查询
     *
     * @param auditLogRealPageQueryDTO
     * @return
     */
    List<AuditLogVO> pageQuery(AuditLogRealPageQueryDTO auditLogRealPageQueryDTO);

    /**
     * 按条件查询所有日志（几乎和上面一样，除了不分页）
     *
     * @param dto
     * @return
     */
    List<AuditLogVO> listForExport(AuditLogRealPageQueryDTO dto);

    /**
     * 查符合条件日志总数
     *
     * @param realPageQueryDTO
     * @return
     */
    long count(AuditLogRealPageQueryDTO realPageQueryDTO);

    /**
     * 插入审计日志
     *
     * @param auditLog
     * @return
     */
    int insert(AuditLog auditLog);
}
