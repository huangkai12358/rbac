package com.ymjrhk.rbac.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.ymjrhk.rbac.constant.SuccessConstant;
import com.ymjrhk.rbac.dto.AuditLogPageQueryDTO;
import com.ymjrhk.rbac.dto.AuditLogRealPageQueryDTO;
import com.ymjrhk.rbac.entity.AuditLog;
import com.ymjrhk.rbac.entity.User;
import com.ymjrhk.rbac.exception.BaseException;
import com.ymjrhk.rbac.mapper.AuditLogMapper;
import com.ymjrhk.rbac.mapper.UserMapper;
import com.ymjrhk.rbac.result.PageResult;
import com.ymjrhk.rbac.service.AuditLogService;
import com.ymjrhk.rbac.service.base.BaseService;
import com.ymjrhk.rbac.vo.AuditLogExcelVO;
import com.ymjrhk.rbac.vo.AuditLogVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.ymjrhk.rbac.constant.PermissionNameConstant.AUTH_LOGIN;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuditLogServiceImpl extends BaseService implements AuditLogService {

    private final AuditLogMapper auditLogMapper;

    private final UserMapper userMapper;

    /**
     * 审计日志分页查询
     *
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

        // 3. 查总数
        long total = auditLogMapper.count(realPageQueryDTO);

        // 4. 查当前页数据
        normalizePage(realPageQueryDTO); // pageNum 和 pageSize 设置默认值兜底

        // 根据 HashMap 白名单生成排序字段和排序方式，防止 SQL 注入
        String orderBy = buildOrderBy(realPageQueryDTO.getSortField(), realPageQueryDTO.getSortOrder()); // 排序字段白名单
        realPageQueryDTO.setOrderBy(orderBy);

        List<AuditLogVO> records = auditLogMapper.pageQuery(realPageQueryDTO);

        return new PageResult(total, records);
    }

    /**
     * 排序构建方法（排序字段白名单）
     *
     * @param sortField
     * @param sortOrder
     * @return
     */
    private String buildOrderBy(String sortField, String sortOrder) {

        // 默认排序（当用户没点排序时）
        String defaultOrder = "create_time desc";

        if (StrUtil.isBlank(sortField) || StrUtil.isBlank(sortOrder)) {
            return defaultOrder;
        }

        // 允许排序的字段（数据库字段）
        Set<String> allowedFields = Set.of(
                "log_seq",
                "username",
                "permission_name",
                "path",
                "method",
                "ip",
                "success",
                "createTime"
        );

        // 前端字段 → 数据库字段映射
        Map<String, String> fieldMap = Map.of(
                "logSeq", "log_seq",
                "username", "username",
                "permissionName", "permission_name",
                "path", "path",
                "method", "method",
                "ip", "ip",
                "success", "success",
                "createTime", "create_time"
        );

        String column = fieldMap.get(sortField);
        if (column == null || !allowedFields.contains(column)) {
            return defaultOrder;
        }

        String order = sortOrder.equalsIgnoreCase("asc") ? "asc" : "desc";

        return column + " " + order;
    }

    /**
     * AOP 插入审计日志
     *
     * @param auditLog
     */
    @Async("auditExecutor")
    @Override
    public void save(AuditLog auditLog) {
        try { // 异步方法内部自己 try-catch
            auditLogMapper.insert(auditLog);
        } catch (Exception e) {
            log.error("异步保存审计日志失败", e);
        }
    }

    /**
     * 登录成功/失败，插入审计日志
     *
     * @param userId
     * @param username
     * @param requestBody
     * @param ip
     * @param success
     * @param errorMessage
     */
    @Async("auditExecutor")
    @Override
    public void saveLoginLog(Long userId,
                             String username,
                             String requestBody,
                             String ip,
                             int success,
                             String errorMessage) {
        AuditLog auditLog = new AuditLog();

        if (userId == null) { // 如果没传 userId，说明登录失败，查 username 对应的 userId
            User user = userMapper.getByUsername(username);
            if (user != null) { // 如果 username 存在，记一下 userId
                auditLog.setUserId(user.getUserId());
            }
        } else { // 如果传了 userId，说明登录成功，直接用
            auditLog.setUserId(userId);
        }

        auditLog.setUsername(username);
        auditLog.setPermissionName(AUTH_LOGIN);
        auditLog.setPath("/api/auth/login");
        auditLog.setMethod("POST");
        auditLog.setIp(ip);
        auditLog.setSuccess(success);
        auditLog.setErrorMessage(errorMessage);

        // 双保险，登录失败才记录 requestBody，防止密码明文保存，是 Controller 层的冗余设计
        if (success == SuccessConstant.FAIL) {
            auditLog.setRequestBody(requestBody);
        }

        try { // 异步方法内部自己 try-catch
            auditLogMapper.insert(auditLog);
        } catch (Exception e) {
            log.error("异步保存登录审计日志失败", e);
        }
    }

    /**
     * 未授权访问，插入审计日志
     *
     * @param auditLog
     */
    @Async("auditExecutor")
    @Override
    public void saveForbiddenLog(AuditLog auditLog) {
        try { // 异步方法内部自己 try-catch
            auditLogMapper.insert(auditLog);
        } catch (Exception e) {
            log.error("异步保存未授权访问审计日志失败", e);
        }
    }

    /**
     * 导出审计日志数据
     *
     * @param auditLogPageQueryDTO
     * @return
     */
    @Override
    public List<AuditLogExcelVO> listForExport(AuditLogPageQueryDTO auditLogPageQueryDTO) {

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
        AuditLogRealPageQueryDTO dto = BeanUtil.copyProperties(auditLogPageQueryDTO, AuditLogRealPageQueryDTO.class);
        dto.setStartTime(startTime);
        dto.setEndTime(endTime);

        // 不跟随分页（导出全部符合条件的数据）
        dto.setPageNum(null);
        dto.setPageSize(null);

        // 动态排序：跟随页面排序
        dto.setOrderBy(buildOrderBy(dto.getSortField(), dto.getSortOrder()));

        // 防止一次性导出过多数据
        int maxExportSize = 50_000;
        long count = auditLogMapper.count(dto);
        if (count > maxExportSize) {
            throw new BaseException("导出数据量过大，请缩小查询范围");
        }

        List<AuditLogVO> list = auditLogMapper.listForExport(dto);

        return list.stream().map(log -> {
            AuditLogExcelVO vo = new AuditLogExcelVO();
            vo.setLogSeq(log.getLogSeq());
            vo.setUsername(log.getUsername());
            vo.setPermissionName(log.getPermissionName());
            vo.setPath(log.getPath());
            vo.setMethod(log.getMethod());
            vo.setRequestBody(log.getRequestBody());
            vo.setIp(log.getIp());
            vo.setSuccess(log.getSuccess() == 1 ? "成功" : "失败");
            vo.setErrorMessage(log.getErrorMessage());
            vo.setCreateTime(log.getCreateTime());
            return vo;
        }).toList();
    }
}
