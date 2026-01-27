package com.ymjrhk.rbac.service.impl;

import com.ymjrhk.rbac.constant.OperateTypeConstant;
import com.ymjrhk.rbac.constant.StatusConstant;
import com.ymjrhk.rbac.entity.Role;
import com.ymjrhk.rbac.entity.RoleHistory;
import com.ymjrhk.rbac.exception.HistoryInsertFailedException;
import com.ymjrhk.rbac.mapper.RoleHistoryMapper;
import com.ymjrhk.rbac.mapper.RoleMapper;
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
class RoleHistoryServiceImplTest {

    @InjectMocks
    private RoleHistoryServiceImpl roleHistoryService;

    @Mock
    private RoleHistoryMapper roleHistoryMapper;

    @Mock
    private RoleMapper roleMapper;

    // ========================= recordHistory() =========================

    /**
     * 正常写入历史表
     */
    @Test
    void recordHistory_success() {
        // given
        Long roleId = 10L;
        Integer operateType = OperateTypeConstant.UPDATE;

        Role role = new Role();
        role.setRoleId(roleId);
        role.setVersion(2);
        role.setRoleName("ADMIN");
        role.setRoleDisplayName("管理员");
        role.setDescription("系统管理员");
        role.setStatus(StatusConstant.ENABLED);
        role.setSecretToken("secret-token");
        role.setUpdateUserId(100L);
        role.setUpdateTime(LocalDateTime.now());

        when(roleMapper.getByRoleId(roleId))
                .thenReturn(role);
        when(roleHistoryMapper.insert(any(RoleHistory.class)))
                .thenReturn(1);

        // when
        roleHistoryService.recordHistory(roleId, operateType);

        // then
        verify(roleMapper).getByRoleId(roleId);

        verify(roleHistoryMapper).insert(argThat(history ->
                history.getRoleId().equals(roleId)
                        && history.getVersion().equals(2)
                        && history.getRoleName().equals("ADMIN")
                        && history.getRoleDisplayName().equals("管理员")
                        && history.getDescription().equals("系统管理员")
                        && history.getStatus().equals(StatusConstant.ENABLED)
                        && history.getSecretToken().equals("secret-token")
                        && history.getOperateType().equals(operateType)
                        && history.getOperatorId().equals(100L)
        ));
    }

    /**
     * 历史表写入失败
     */
    @Test
    void recordHistory_insertFail_throwException() {
        // given
        Long roleId = 10L;
        Integer operateType = OperateTypeConstant.CREATE;

        Role role = new Role();
        role.setRoleId(roleId);
        role.setVersion(1);
        role.setRoleName("USER");

        when(roleMapper.getByRoleId(roleId))
                .thenReturn(role);
        when(roleHistoryMapper.insert(any(RoleHistory.class)))
                .thenReturn(0); // 模拟插入失败

        // then
        assertThrows(HistoryInsertFailedException.class,
                () -> roleHistoryService.recordHistory(roleId, operateType));
    }

}
