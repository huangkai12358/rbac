package com.ymjrhk.rbac.service.impl;

import com.ymjrhk.rbac.constant.OperateTypeConstant;
import com.ymjrhk.rbac.constant.PermissionTypeConstant;
import com.ymjrhk.rbac.constant.StatusConstant;
import com.ymjrhk.rbac.entity.Permission;
import com.ymjrhk.rbac.entity.PermissionHistory;
import com.ymjrhk.rbac.exception.HistoryInsertFailedException;
import com.ymjrhk.rbac.mapper.PermissionHistoryMapper;
import com.ymjrhk.rbac.mapper.PermissionMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PermissionHistoryServiceImplTest {

    @InjectMocks
    private PermissionHistoryServiceImpl permissionHistoryService;

    @Mock
    private PermissionHistoryMapper permissionHistoryMapper;

    @Mock
    private PermissionMapper permissionMapper;

    // ========================= record() =========================

    /**
     * 正常写入历史表
     */
    @Test
    void record_success() {
        // given
        Long permissionId = 100L;
        Integer operateType = OperateTypeConstant.UPDATE;

        Permission permission = new Permission();
        permission.setPermissionId(permissionId);
        permission.setVersion(3);
        permission.setPermissionName("USER:VIEW");
        permission.setPermissionDisplayName("查看用户");
        permission.setDescription("查看用户接口");
        permission.setStatus(StatusConstant.ENABLED);
        permission.setSecretToken("secret-token");
        permission.setType(PermissionTypeConstant.ACTION);
        permission.setParentId(1L);
        permission.setPath("/api/users/**");
        permission.setMethod("GET");
        permission.setSort(10);
        permission.setUpdateUserId(200L);
        permission.setUpdateTime(LocalDateTime.now());

        when(permissionMapper.getByPermissionId(permissionId))
                .thenReturn(permission);
        when(permissionHistoryMapper.insert(any(PermissionHistory.class)))
                .thenReturn(1);

        // when
        permissionHistoryService.record(permissionId, operateType);

        // then
        verify(permissionMapper).getByPermissionId(permissionId);

        verify(permissionHistoryMapper).insert(argThat(history ->
                history.getPermissionId().equals(permissionId)
                        && history.getVersion().equals(3)
                        && history.getPermissionName().equals("USER:VIEW")
                        && history.getPermissionDisplayName().equals("查看用户")
                        && history.getDescription().equals("查看用户接口")
                        && history.getStatus().equals(StatusConstant.ENABLED)
                        && history.getSecretToken().equals("secret-token")
                        && history.getType().equals(PermissionTypeConstant.ACTION)
                        && history.getParentId().equals(1L)
                        && history.getPath().equals("/api/users/**")
                        && history.getMethod().equals("GET")
                        && history.getSort().equals(10)
                        && history.getOperateType().equals(operateType)
                        && history.getOperatorId().equals(200L)
        ));
    }

    /**
     * 历史表写入失败
     */
    @Test
    void record_insertFail_throwException() {
        // given
        Long permissionId = 100L;
        Integer operateType = OperateTypeConstant.CREATE;

        Permission permission = new Permission();
        permission.setPermissionId(permissionId);
        permission.setPermissionName("USER:CREATE");

        when(permissionMapper.getByPermissionId(permissionId))
                .thenReturn(permission);
        when(permissionHistoryMapper.insert(any(PermissionHistory.class)))
                .thenReturn(0); // 模拟写入失败

        // then
        assertThrows(HistoryInsertFailedException.class,
                () -> permissionHistoryService.record(permissionId, operateType));
    }


}
