package com.ymjrhk.rbac.service.impl;

import com.github.pagehelper.Page;
import com.ymjrhk.rbac.constant.SuccessConstant;
import com.ymjrhk.rbac.dto.AuditLogPageQueryDTO;
import com.ymjrhk.rbac.dto.AuditLogRealPageQueryDTO;
import com.ymjrhk.rbac.entity.AuditLog;
import com.ymjrhk.rbac.entity.User;
import com.ymjrhk.rbac.mapper.AuditLogMapper;
import com.ymjrhk.rbac.mapper.UserMapper;
import com.ymjrhk.rbac.result.PageResult;
import com.ymjrhk.rbac.vo.AuditLogVO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static com.ymjrhk.rbac.constant.PermissionNameConstant.AUTH_LOGIN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuditLogServiceImplTest {

    @InjectMocks
    private AuditLogServiceImpl auditLogService;

    @Mock
    private AuditLogMapper auditLogMapper;

    @Mock
    private UserMapper userMapper; // 构造器需要，但 pageQuery 用不到

    // ========================= pageQuery() =========================

    /**
     * 正常分页 + 日期转换
     */
    @Test
    void pageQuery_withDateRange_success() {
        // given
        AuditLogPageQueryDTO dto = new AuditLogPageQueryDTO();
        dto.setStartDate(LocalDate.of(2026, 1, 1));
        dto.setEndDate(LocalDate.of(2026, 1, 31));
        dto.setPageNum(1);
        dto.setPageSize(10);

        AuditLogVO vo = new AuditLogVO();
        vo.setUsername("zhangsan");

        Page<AuditLogVO> page = new Page<>();
        page.setTotal(1);
        page.add(vo);

        when(auditLogMapper.pageQuery(any(AuditLogRealPageQueryDTO.class)))
                .thenReturn(page);

        // when
        PageResult result = auditLogService.pageQuery(dto);

        // then
        assertEquals(1L, result.getTotal());
        assertEquals(1, result.getRecords().size());

        verify(auditLogMapper).pageQuery(argThat(realDto ->
                realDto.getStartTime().equals(
                        LocalDate.of(2026, 1, 1).atStartOfDay()
                )
                        && realDto.getEndTime().toLocalDate().equals(
                        LocalDate.of(2026, 1, 31)
                )
                        && realDto.getPageNum() == 1
                        && realDto.getPageSize() == 10
        ));
    }

    /**
     * startDate / endDate 都为空（兜底场景）
     */
    @Test
    void pageQuery_dateNull_useNullTime() {
        // given
        AuditLogPageQueryDTO dto = new AuditLogPageQueryDTO();
        // 不设置 startDate / endDate
        dto.setPageNum(1);
        dto.setPageSize(10);

        Page<AuditLogVO> emptyPage = new Page<>();
        emptyPage.setTotal(0);

        when(auditLogMapper.pageQuery(any(AuditLogRealPageQueryDTO.class)))
                .thenReturn(emptyPage);

        // when
        PageResult result = auditLogService.pageQuery(dto);

        // then
        assertEquals(0L, result.getTotal());
        assertTrue(result.getRecords().isEmpty());

        verify(auditLogMapper).pageQuery(argThat(realDto ->
                realDto.getStartTime() == null
                        && realDto.getEndTime() == null
        ));
    }

    /**
     * 分页参数为空 → normalizePage 生效
     */
    @Test
    void pageQuery_pageParamNull_useDefault() {
        // given
        AuditLogPageQueryDTO dto = new AuditLogPageQueryDTO();
        // pageNum / pageSize 都不设

        Page<AuditLogVO> emptyPage = new Page<>();
        emptyPage.setTotal(0);

        when(auditLogMapper.pageQuery(any(AuditLogRealPageQueryDTO.class)))
                .thenReturn(emptyPage);

        // when
        auditLogService.pageQuery(dto);

        // then
        verify(auditLogMapper).pageQuery(argThat(realDto ->
                realDto.getPageNum() == 1
                        && realDto.getPageSize() == 10
        ));
    }

    // ========================= save() =========================

    /**
     * 测试插入日志
     */
    @Test
    void save_success_callInsert() {
        // given
        AuditLog auditLog = new AuditLog();
        auditLog.setUserId(1L);
        auditLog.setUsername("zhangsan");

        // when
        auditLogService.save(auditLog);

        // then
        verify(auditLogMapper).insert(auditLog);
    }

    // ========================= saveLoginLog() =========================

    /**
     * 登录成功（userId 已知）
     */
    @Test
    void saveLoginLog_success_useGivenUserId() {
        // given
        Long userId = 1L;
        String username = "zhangsan";

        // when
        auditLogService.saveLoginLog(
                userId,
                username,
                "{\"password\":\"123456\"}",
                "127.0.0.1",
                SuccessConstant.SUCCESS,
                null
        );

        // then
        verify(auditLogMapper).insert(argThat(log ->
                log.getUserId().equals(userId)
                        && log.getUsername().equals(username)
                        && log.getPermissionName().equals(AUTH_LOGIN)
                        && log.getPath().equals("/api/auth/login")
                        && log.getMethod().equals("POST")
                        && log.getIp().equals("127.0.0.1")
                        && log.getSuccess() == SuccessConstant.SUCCESS
                        && log.getRequestBody() == null // 成功时不记录
        ));

        verify(userMapper, never()).getByUsername(anyString());
    }

    /**
     * 登录失败 + username 存在
     */
    @Test
    void saveLoginLog_fail_usernameExist_setUserId() {
        // given
        String username = "zhangsan";

        User user = new User();
        user.setUserId(2L);
        user.setUsername(username);

        when(userMapper.getByUsername(username))
                .thenReturn(user);

        // when
        auditLogService.saveLoginLog(
                null,
                username,
                "{\"password\":\"wrong\"}",
                "127.0.0.1",
                SuccessConstant.FAIL,
                "密码错误"
        );

        // then
        verify(auditLogMapper).insert(argThat(log ->
                log.getUserId().equals(2L)
                        && log.getUsername().equals(username)
                        && log.getSuccess() == SuccessConstant.FAIL
                        && log.getRequestBody().equals("{\"password\":\"wrong\"}")
                        && log.getErrorMessage().equals("密码错误")
        ));

        verify(userMapper).getByUsername(username);
    }

    /**
     * 登录失败 + username 不存在
     */
    @Test
    void saveLoginLog_fail_usernameNotExist_userIdNull() {
        // given
        String username = "ghost";

        when(userMapper.getByUsername(username))
                .thenReturn(null);

        // when
        auditLogService.saveLoginLog(
                null,
                username,
                "{\"password\":\"wrong\"}",
                "127.0.0.1",
                SuccessConstant.FAIL,
                "账号不存在"
        );

        // then
        verify(auditLogMapper).insert(argThat(log ->
                log.getUserId() == null
                        && log.getUsername().equals(username)
                        && log.getSuccess() == SuccessConstant.FAIL
                        && log.getRequestBody().equals("{\"password\":\"wrong\"}")
        ));
    }

    // ========================= saveForbiddenLog() =========================

    /**
     * 测试未授权访问日志插入
     */
    @Test
    void saveForbiddenLog_success_insertAuditLog() {
        // given
        Long userId = 1L;
        String username = "zhangsan";
        String requestURI = "/api/users/1";
        String method = "DELETE";
        String requestBody = "{\"id\":1}";
        String ip = "127.0.0.1";
        int success = SuccessConstant.FAIL;
        String errorMessage = "无权限访问";

        // when
        auditLogService.saveForbiddenLog(
                userId,
                username,
                requestURI,
                method,
                requestBody,
                ip,
                success,
                errorMessage
        );

        // then
        verify(auditLogMapper).insert(argThat(log ->
                log.getUserId().equals(userId)
                        && log.getUsername().equals(username)
                        && log.getPath().equals(requestURI)
                        && log.getMethod().equals(method)
                        && log.getRequestBody().equals(requestBody)
                        && log.getIp().equals(ip)
                        && log.getSuccess() == success
                        && log.getErrorMessage().equals(errorMessage)
        ));
    }


}
