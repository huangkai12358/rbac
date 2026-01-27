package com.ymjrhk.rbac.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.page.PageMethod;
import com.ymjrhk.rbac.constant.SuccessConstant;
import com.ymjrhk.rbac.dto.AuditLogPageQueryDTO;
import com.ymjrhk.rbac.dto.AuditLogRealPageQueryDTO;
import com.ymjrhk.rbac.entity.AuditLog;
import com.ymjrhk.rbac.entity.User;
import com.ymjrhk.rbac.mapper.AuditLogMapper;
import com.ymjrhk.rbac.mapper.UserMapper;
import com.ymjrhk.rbac.result.PageResult;
import com.ymjrhk.rbac.service.AuditLogService;
import com.ymjrhk.rbac.service.base.BaseService;
import com.ymjrhk.rbac.vo.AuditLogVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;

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

        // 3. 分页
        normalizePage(realPageQueryDTO);

        PageMethod.startPage(realPageQueryDTO.getPageNum(), realPageQueryDTO.getPageSize());
        Page<AuditLogVO> page = auditLogMapper.pageQuery(realPageQueryDTO);

        return new PageResult(page.getTotal(), page.getResult());
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
}
