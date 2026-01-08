package com.ymjrhk.rbac.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.ymjrhk.rbac.dto.AuditLogPageQueryDTO;
import com.ymjrhk.rbac.dto.AuditLogRealPageQueryDTO;
import com.ymjrhk.rbac.mapper.AuditLogMapper;
import com.ymjrhk.rbac.result.PageResult;
import com.ymjrhk.rbac.service.AuditLogService;
import com.ymjrhk.rbac.service.base.BaseService;
import com.ymjrhk.rbac.vo.AuditLogVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuditLogServiceImpl extends BaseService implements AuditLogService {

    private final AuditLogMapper auditLogMapper;

    /**
     * 审计日志分页查询
     * @param auditLogPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(AuditLogPageQueryDTO auditLogPageQueryDTO) {
        // 1. 日期 → 时间（业务兜底）
        LocalDateTime startTime = null;
        LocalDateTime endTime = null;

        if (auditLogPageQueryDTO.getStartDate() != null) {
            startTime = auditLogPageQueryDTO.getStartDate().atStartOfDay(); // 2026-01-01 00:00:00

        }
        if (auditLogPageQueryDTO.getEndDate() != null) {
            endTime = auditLogPageQueryDTO.getEndDate().atTime(LocalTime.MAX); // 2026-01-31 23:59:59.999999999

        }

        // 2. 交给 Mapper 用的真正查询对象
        AuditLogRealPageQueryDTO realPageQueryDTO = BeanUtil.copyProperties(auditLogPageQueryDTO, AuditLogRealPageQueryDTO.class);
        realPageQueryDTO.setStartTime(startTime);
        realPageQueryDTO.setEndTime(endTime);

        // 3. 分页
        normalizePage(realPageQueryDTO);

        PageHelper.startPage(realPageQueryDTO.getPageNum(), realPageQueryDTO.getPageSize());
        Page<AuditLogVO> page = auditLogMapper.pageQuery(realPageQueryDTO);

        return new PageResult(page.getTotal(), page.getResult());
    }
}
