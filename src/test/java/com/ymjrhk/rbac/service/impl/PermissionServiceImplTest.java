package com.ymjrhk.rbac.service.impl;

import com.github.pagehelper.Page;
import com.ymjrhk.rbac.constant.OperateTypeConstant;
import com.ymjrhk.rbac.constant.PermissionTypeConstant;
import com.ymjrhk.rbac.constant.StatusConstant;
import com.ymjrhk.rbac.context.LoginUser;
import com.ymjrhk.rbac.context.UserContext;
import com.ymjrhk.rbac.dto.PermissionCreateDTO;
import com.ymjrhk.rbac.dto.PermissionDTO;
import com.ymjrhk.rbac.dto.PermissionPageQueryDTO;
import com.ymjrhk.rbac.entity.Permission;
import com.ymjrhk.rbac.exception.*;
import com.ymjrhk.rbac.mapper.PermissionMapper;
import com.ymjrhk.rbac.result.PageResult;
import com.ymjrhk.rbac.service.PermissionHistoryService;
import com.ymjrhk.rbac.vo.PermissionVO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PermissionServiceImplTest {

    @InjectMocks
    private PermissionServiceImpl permissionService;

    @Mock
    private PermissionMapper permissionMapper;

    @Mock
    private PermissionHistoryService permissionHistoryService;

    @BeforeEach
    void setUp() {
        UserContext.set(new LoginUser(100L, "admin"));
    }

    @AfterEach
    void tearDown() {
        UserContext.clear();
    }

    // ========================= create() =========================

    /**
     * 创建权限成功
     */
    @Test
    void create_success() {
        // given
        PermissionCreateDTO dto = new PermissionCreateDTO();
        dto.setPermissionName("USER:VIEW");
        dto.setPermissionDisplayName("查看用户");
        dto.setType(PermissionTypeConstant.ACTION);
        dto.setPath("/api/users/**");
        dto.setMethod("GET");

        // insert 成功，并模拟 MyBatis 回填 permissionId
        when(permissionMapper.insert(any(Permission.class)))
                .thenAnswer(invocation -> {
                    Permission p = invocation.getArgument(0);
                    p.setPermissionId(10L); // 模拟数据库生成的主键
                    return 1;
                });

        // when
        Long permissionId = permissionService.create(dto);

        // then
        assertEquals(10L, permissionId);

        verify(permissionMapper).insert(argThat(p ->
                p.getPermissionName().equals("USER:VIEW")
                        && p.getCreateUserId().equals(100L)
                        && p.getUpdateUserId().equals(100L)
                        && p.getSecretToken() != null
        ));

        verify(permissionHistoryService)
                .record(10L, OperateTypeConstant.CREATE);
    }

    /**
     * 创建权限失败
     */
    @Test
    void create_insertFail_throwException() {
        // given
        PermissionCreateDTO dto = new PermissionCreateDTO();
        dto.setPermissionName("USER:VIEW");

        when(permissionMapper.insert(any(Permission.class)))
                .thenReturn(0); // 插入失败

        // then
        assertThrows(PermissionCreateFailedException.class,
                () -> permissionService.create(dto));

        verify(permissionHistoryService, never())
                .record(anyLong(), any());
    }

    // ========================= pageQuery() =========================

    /**
     * 分页查询成功
     */
    @Test
    void pageQuery_success() {
        // given
        PermissionPageQueryDTO dto = new PermissionPageQueryDTO();
        dto.setPageNum(1);
        dto.setPageSize(10);

        Page<Permission> page = new Page<>();
        page.setTotal(2);

        Permission p1 = new Permission();
        p1.setPermissionId(1L);
        p1.setPermissionName("USER:VIEW");

        Permission p2 = new Permission();
        p2.setPermissionId(2L);
        p2.setPermissionName("USER:CREATE");

        page.add(p1);
        page.add(p2);

        when(permissionMapper.pageQuery(dto)).thenReturn(page);

        // when
        PageResult result = permissionService.pageQuery(dto);

        // then
        assertNotNull(result);
        assertEquals(2, result.getTotal());

        List<PermissionVO> records = result.getRecords();
        assertEquals(2, records.size());
        assertEquals("USER:VIEW", records.get(0).getPermissionName());
        assertEquals("USER:CREATE", records.get(1).getPermissionName());

        verify(permissionMapper).pageQuery(dto);
    }

    /**
     * 分页参数兜底
     */
    @Test
    void pageQuery_pageParamInvalid_useDefault() {
        // given
        PermissionPageQueryDTO dto = new PermissionPageQueryDTO();
        // pageNum / pageSize 不设置（或设置成非法值）

        Page<Permission> emptyPage = new Page<>();
        emptyPage.setTotal(0);

        when(permissionMapper.pageQuery(dto)).thenReturn(emptyPage);

        // when
        PageResult result = permissionService.pageQuery(dto);

        // then
        assertEquals(0, result.getTotal());
        assertTrue(result.getRecords().isEmpty());

        // 关键：normalizePage 生效
        assertNotNull(dto.getPageNum());
        assertNotNull(dto.getPageSize());
        assertTrue(dto.getPageNum() >= 1);
        assertTrue(dto.getPageSize() > 0);

        verify(permissionMapper).pageQuery(dto);
    }

    // ========================= getByPermissionId() =========================

    /**
     * 权限存在
     */
    @Test
    void getByPermissionId_success() {
        // given
        Long permissionId = 1L;

        Permission permission = new Permission();
        permission.setPermissionId(permissionId);
        permission.setPermissionName("USER:VIEW");
        permission.setPermissionDisplayName("查看用户");
        permission.setType(PermissionTypeConstant.ACTION);
        permission.setPath("/api/users/**");
        permission.setMethod("GET");

        when(permissionMapper.getByPermissionId(permissionId))
                .thenReturn(permission);

        // when
        PermissionVO result = permissionService.getByPermissionId(permissionId);

        // then
        assertNotNull(result);
        assertEquals(permissionId, result.getPermissionId());
        assertEquals("USER:VIEW", result.getPermissionName());
        assertEquals("查看用户", result.getPermissionDisplayName());
        assertEquals(PermissionTypeConstant.ACTION, result.getType());
        assertEquals("/api/users/**", result.getPath());
        assertEquals("GET", result.getMethod());

        verify(permissionMapper).getByPermissionId(permissionId);
    }

    /**
     * 权限不存在
     */
    @Test
    void getByPermissionId_permissionNotExist_throwException() {
        // given
        Long permissionId = 99L;

        when(permissionMapper.getByPermissionId(permissionId))
                .thenReturn(null);

        // then
        assertThrows(PermissionNotExistException.class,
                () -> permissionService.getByPermissionId(permissionId));

        verify(permissionMapper).getByPermissionId(permissionId);
    }

    // ========================= update() =========================

    /**
     * 更新成功
     */
    @Test
    void update_success() {
        // given
        Long permissionId = 1L;

        Permission dbPermission = new Permission();
        dbPermission.setPermissionId(permissionId);
        dbPermission.setStatus(StatusConstant.ENABLED);

        PermissionDTO dto = new PermissionDTO();
        dto.setPermissionName("USER:VIEW");
        dto.setPermissionDisplayName("查看用户");
        dto.setPath("/api/users/**");
        dto.setMethod("GET");
        dto.setVersion(1);
        dto.setSecretToken("old-token");

        when(permissionMapper.getByPermissionId(permissionId))
                .thenReturn(dbPermission);
        when(permissionMapper.update(any(Permission.class)))
                .thenReturn(1);

        // when
        permissionService.update(permissionId, dto);

        // then
        verify(permissionMapper).update(argThat(p ->
                p.getPermissionId().equals(permissionId)
                        && p.getPermissionName().equals("USER:VIEW")
                        && p.getVersion().equals(1)
                        && p.getSecretToken().equals("old-token")
                        && p.getNewSecretToken() != null
                        && p.getUpdateUserId().equals(100L)
        ));

        verify(permissionHistoryService)
                .record(permissionId, OperateTypeConstant.UPDATE);
    }

    /**
     * 权限不存在
     */
    @Test
    void update_permissionNotExist_throwException() {
        // given
        Long permissionId = 99L;

        when(permissionMapper.getByPermissionId(permissionId))
                .thenReturn(null);

        // then
        assertThrows(PermissionNotExistException.class,
                () -> permissionService.update(permissionId, new PermissionDTO()));

        verify(permissionMapper, never()).update(any());
        verify(permissionHistoryService, never()).record(anyLong(), any());
    }

    /**
     * 权限被禁用
     */
    @Test
    void update_permissionDisabled_throwException() {
        // given
        Long permissionId = 2L;

        Permission dbPermission = new Permission();
        dbPermission.setPermissionId(permissionId);
        dbPermission.setStatus(StatusConstant.DISABLED);

        when(permissionMapper.getByPermissionId(permissionId))
                .thenReturn(dbPermission);

        // then
        assertThrows(PermissionForbiddenException.class,
                () -> permissionService.update(permissionId, new PermissionDTO()));

        verify(permissionMapper, never()).update(any());
        verify(permissionHistoryService, never()).record(anyLong(), any());
    }

    /**
     * 更新失败（乐观锁冲突）
     */
    @Test
    void update_updateFail_throwException() {
        // given
        Long permissionId = 3L;

        Permission dbPermission = new Permission();
        dbPermission.setPermissionId(permissionId);
        dbPermission.setStatus(StatusConstant.ENABLED);

        PermissionDTO dto = new PermissionDTO();
        dto.setVersion(1);
        dto.setSecretToken("old-token");

        when(permissionMapper.getByPermissionId(permissionId))
                .thenReturn(dbPermission);
        when(permissionMapper.update(any(Permission.class)))
                .thenReturn(0); // 更新失败

        // then
        assertThrows(UpdateFailedException.class,
                () -> permissionService.update(permissionId, dto));

        verify(permissionHistoryService, never())
                .record(anyLong(), any());
    }

    // ========================= changeStatus() =========================

    /**
     * 状态修改成功
     */
    @Test
    void changeStatus_success() {
        // given
        Long permissionId = 1L;
        Integer newStatus = StatusConstant.DISABLED;

        Permission dbPermission = new Permission();
        dbPermission.setPermissionId(permissionId);
        dbPermission.setStatus(StatusConstant.ENABLED);
        dbPermission.setVersion(1);
        dbPermission.setSecretToken("old-token");

        when(permissionMapper.getByPermissionId(permissionId))
                .thenReturn(dbPermission);
        when(permissionMapper.update(any(Permission.class)))
                .thenReturn(1);

        // when
        permissionService.changeStatus(permissionId, newStatus);

        // then
        verify(permissionMapper).update(argThat(p ->
                p.getPermissionId().equals(permissionId)
                        && p.getStatus().equals(newStatus)
                        && p.getVersion().equals(1)
                        && p.getSecretToken().equals("old-token")
                        && p.getNewSecretToken() != null
                        && p.getUpdateUserId().equals(100L)
        ));

        verify(permissionHistoryService)
                .record(permissionId, OperateTypeConstant.UPDATE);
    }

    /**
     * 权限不存在
     */
    @Test
    void changeStatus_permissionNotExist_throwException() {
        // given
        Long permissionId = 99L;

        when(permissionMapper.getByPermissionId(permissionId))
                .thenReturn(null);

        // then
        assertThrows(PermissionNotExistException.class,
                () -> permissionService.changeStatus(permissionId, StatusConstant.DISABLED));

        verify(permissionMapper, never()).update(any());
        verify(permissionHistoryService, never()).record(anyLong(), any());
    }

    /**
     * 状态未改变
     */
    @Test
    void changeStatus_statusNotChange_throwException() {
        // given
        Long permissionId = 2L;

        Permission dbPermission = new Permission();
        dbPermission.setPermissionId(permissionId);
        dbPermission.setStatus(StatusConstant.ENABLED);

        when(permissionMapper.getByPermissionId(permissionId))
                .thenReturn(dbPermission);

        // then
        assertThrows(StatusNotChangeException.class,
                () -> permissionService.changeStatus(permissionId, StatusConstant.ENABLED));

        verify(permissionMapper, never()).update(any());
        verify(permissionHistoryService, never()).record(anyLong(), any());
    }

    /**
     * update 失败（乐观锁冲突）
     */
    @Test
    void changeStatus_updateFail_throwException() {
        // given
        Long permissionId = 3L;

        Permission dbPermission = new Permission();
        dbPermission.setPermissionId(permissionId);
        dbPermission.setStatus(StatusConstant.ENABLED);
        dbPermission.setVersion(1);
        dbPermission.setSecretToken("old-token");

        when(permissionMapper.getByPermissionId(permissionId))
                .thenReturn(dbPermission);
        when(permissionMapper.update(any(Permission.class)))
                .thenReturn(0); // 更新失败

        // then
        assertThrows(UpdateFailedException.class,
                () -> permissionService.changeStatus(permissionId, StatusConstant.DISABLED));

        verify(permissionHistoryService, never())
                .record(anyLong(), any());
    }










}
