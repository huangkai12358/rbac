package com.ymjrhk.rbac.service.impl;

import com.ymjrhk.rbac.context.LoginUser;
import com.ymjrhk.rbac.context.UserContext;
import com.ymjrhk.rbac.entity.Role;
import com.ymjrhk.rbac.exception.*;
import com.ymjrhk.rbac.mapper.*;
import com.ymjrhk.rbac.vo.PermissionVO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;

import static com.ymjrhk.rbac.constant.RoleNameConstant.SUPER_ADMIN;
import static com.ymjrhk.rbac.constant.StatusConstant.DISABLED;
import static com.ymjrhk.rbac.constant.StatusConstant.ENABLED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RolePermissionServiceImplTest {

    @InjectMocks
    private RolePermissionServiceImpl rolePermissionService;

    @Mock
    private RoleMapper roleMapper;

    @Mock
    private RolePermissionMapper rolePermissionMapper;

    @Mock
    private PermissionMapper permissionMapper;

    @Mock
    private UserMapper userMapper;

    @Mock
    private UserRoleMapper userRoleMapper;

    @BeforeEach
    void setUp() {
        // 当前操作人
        UserContext.set(new LoginUser(100L, "admin"));
    }

    @AfterEach
    void tearDown() {
        UserContext.clear();
    }

    // ========================= assignPermissionsToRole() =========================

    /*
    | 编号 | 场景                     |
    | -- | ---------------------- |
    | T1 | 角色不存在                  |
    | T2 | 角色被禁用                  |
    | T3 | 给超级管理员角色分配权限           |
    | T4 | 提交的权限不存在 / 被禁用         |
    | T5 | 普通管理员越权分配权限            |
    | T6 | permissionIds 为空（只删不加） |
    | T7 | 普通管理员合法分配成功            |
    | T8 | 超级管理员用户合法分配成功          |
    | T9 | batchInsert 数量不一致      |
    */

    /**
     * 角色不存在
     */
    @Test
    void assignPermissions_roleNotExist_throwException() {
        List<Long> permissionIds = List.of(1L);

        when(roleMapper.getByRoleId(1L)).thenReturn(null);

        assertThrows(RoleNotExistException.class,
                () -> rolePermissionService.assignPermissionsToRole(1L, permissionIds));
    }

    /**
     * 角色被禁用
     */
    @Test
    void assignPermissions_roleDisabled_throwException() {
        Role role = new Role();
        role.setRoleId(1L);
        role.setStatus(DISABLED);

        List<Long> permissionIds = List.of(1L);

        when(roleMapper.getByRoleId(1L)).thenReturn(role);

        assertThrows(RoleForbiddenException.class,
                () -> rolePermissionService.assignPermissionsToRole(1L, permissionIds));
    }

    /**
     * 给超级管理员角色分配权限（禁止）
     */
    @Test
    void assignPermissions_superAdminRole_throwException() {
        Role role = new Role();
        role.setRoleId(1L);
        role.setStatus(ENABLED);
        role.setRoleName(SUPER_ADMIN);

        List<Long> permissionIds = List.of(1L);

        when(roleMapper.getByRoleId(1L)).thenReturn(role);

        assertThrows(AssignmentPermissionToSuperAdminException.class,
                () -> rolePermissionService.assignPermissionsToRole(1L, permissionIds));
    }

    /**
     * 权限不存在 / 被禁用
     */
    @Test
    void assignPermissions_permissionNotExistOrDisabled_throwException() {
        Role role = new Role();
        role.setRoleId(1L);
        role.setStatus(ENABLED);
        role.setRoleName("ADMIN");

        when(roleMapper.getByRoleId(1L)).thenReturn(role);

        when(permissionMapper.selectEnabledPermissionIds(List.of(1L, 2L)))
                .thenReturn(List.of(1L));

        List<Long> permissionIds = List.of(1L, 2L);

        assertThrows(PermissionNotExistOrDisabledException.class,
                () -> rolePermissionService.assignPermissionsToRole(1L, permissionIds));
    }

    /**
     * 普通管理员越权分配权限
     */
    @Test
    void assignPermissions_normalAdmin_assignUnownedPermission_throwException() {
        Role role = new Role();
        role.setRoleId(1L);
        role.setStatus(ENABLED);
        role.setRoleName("ADMIN");

        when(roleMapper.getByRoleId(1L)).thenReturn(role);

        when(permissionMapper.selectEnabledPermissionIds(List.of(10L)))
                .thenReturn(List.of(10L));

        when(userRoleMapper.userHasRole(100L, SUPER_ADMIN))
                .thenReturn(false);

        // 我只拥有 permissionId = 20
        when(userMapper.selectPermissionIdsByUserIdAndStatus(100L, ENABLED))
                .thenReturn(List.of(20L));

        List<Long> permissionIds = List.of(10L);

        assertThrows(AssignmentNotOwnedPermissionException.class,
                () -> rolePermissionService.assignPermissionsToRole(1L, permissionIds));
    }

    /**
     * permissionIds 为空（只删不加）
     */
    @Test
    void assignPermissions_emptyPermissionIds_onlyDelete() {
        Role role = new Role();
        role.setRoleId(1L);
        role.setStatus(ENABLED);
        role.setRoleName("ADMIN");

        when(roleMapper.getByRoleId(1L)).thenReturn(role);

        when(userRoleMapper.userHasRole(100L, SUPER_ADMIN))
                .thenReturn(true);

        when(permissionMapper.selectAllEnabledPermissionIds())
                .thenReturn(List.of(1L, 2L));

        when(rolePermissionMapper.selectPermissionIdsByRoleIdAndStatus(1L, ENABLED))
                .thenReturn(List.of(1L, 2L));

        rolePermissionService.assignPermissionsToRole(1L, List.of());

        verify(rolePermissionMapper)
                .deleteByRoleIdAndPermissionIds(1L, Set.of(1L, 2L));

        verify(rolePermissionMapper, never()).batchInsert(any());
    }

    /**
     * 普通管理员，合法分配成功
     */
    @Test
    void assignPermissions_normalAdmin_success() {
        Role role = new Role();
        role.setRoleId(1L);
        role.setStatus(ENABLED);
        role.setRoleName("ADMIN");

        when(roleMapper.getByRoleId(1L)).thenReturn(role);

        when(permissionMapper.selectEnabledPermissionIds(List.of(10L)))
                .thenReturn(List.of(10L));

        when(userRoleMapper.userHasRole(100L, SUPER_ADMIN))
                .thenReturn(false);

        when(userMapper.selectPermissionIdsByUserIdAndStatus(100L, ENABLED))
                .thenReturn(List.of(10L));

        when(rolePermissionMapper.selectPermissionIdsByRoleIdAndStatus(1L, ENABLED))
                .thenReturn(List.of());

        when(rolePermissionMapper.batchInsert(anyList()))
                .thenReturn(1);

        rolePermissionService.assignPermissionsToRole(1L, List.of(10L));

        verify(rolePermissionMapper).batchInsert(argThat(list ->
                list.size() == 1 &&
                        list.getFirst().getRoleId().equals(1L) &&
                        list.getFirst().getPermissionId().equals(10L) &&
                        list.getFirst().getCreateUserId().equals(100L)
        ));
    }

    /**
     * 超级管理员用户，合法分配成功
     */
    @Test
    void assignPermissions_superAdminUser_success() {
        Role role = new Role();
        role.setRoleId(1L);
        role.setStatus(ENABLED);
        role.setRoleName("ADMIN");

        when(roleMapper.getByRoleId(1L)).thenReturn(role);

        when(permissionMapper.selectEnabledPermissionIds(List.of(1L, 2L)))
                .thenReturn(List.of(1L, 2L));

        when(userRoleMapper.userHasRole(100L, SUPER_ADMIN))
                .thenReturn(true);

        when(permissionMapper.selectAllEnabledPermissionIds())
                .thenReturn(List.of(1L, 2L, 3L));

        when(rolePermissionMapper.selectPermissionIdsByRoleIdAndStatus(1L, ENABLED))
                .thenReturn(List.of(3L));

        when(rolePermissionMapper.batchInsert(anyList()))
                .thenReturn(2);

        rolePermissionService.assignPermissionsToRole(1L, List.of(1L, 2L));

        verify(rolePermissionMapper)
                .deleteByRoleIdAndPermissionIds(1L, Set.of(3L));
    }

    /**
     * batchInsert 失败兜底
     */
    @Test
    void assignPermissions_batchInsertFailed_throwException() {
        Role role = new Role();
        role.setRoleId(1L);
        role.setStatus(ENABLED);
        role.setRoleName("ADMIN");

        when(roleMapper.getByRoleId(1L)).thenReturn(role);

        when(permissionMapper.selectEnabledPermissionIds(List.of(1L)))
                .thenReturn(List.of(1L));

        when(userRoleMapper.userHasRole(100L, SUPER_ADMIN))
                .thenReturn(false);

        when(userMapper.selectPermissionIdsByUserIdAndStatus(100L, ENABLED))
                .thenReturn(List.of(1L));

        when(rolePermissionMapper.selectPermissionIdsByRoleIdAndStatus(1L, ENABLED))
                .thenReturn(List.of());

        when(rolePermissionMapper.batchInsert(anyList()))
                .thenReturn(0);

        List<Long> permissionIds = List.of(1L);

        assertThrows(AssignmentPermissionFailedException.class,
                () -> rolePermissionService.assignPermissionsToRole(1L, permissionIds));
    }

    // ========================= getRolePermissions() =========================

    /**
     * 角色不存在 → 抛异常
     */
    @Test
    void getRolePermissions_roleNotExist_throwException() {
        // given
        Long roleId = 1L;
        when(roleMapper.getByRoleId(roleId)).thenReturn(null);

        // when & then
        assertThrows(RoleNotExistException.class,
                () -> rolePermissionService.getRolePermissions(roleId));

        // 确保不会继续查权限
        verify(permissionMapper, never()).listAllActivePermissions();
        verify(rolePermissionMapper, never()).selectPermissionsByRoleId(any());
    }

    /**
     * 超级管理员角色 → 返回全部权限
     */
    @Test
    void getRolePermissions_superAdmin_returnAllPermissions() {
        // given
        Long roleId = 1L;

        Role role = new Role();
        role.setRoleId(roleId);
        role.setRoleName(SUPER_ADMIN);

        PermissionVO p1 = new PermissionVO();
        p1.setPermissionId(1L);
        PermissionVO p2 = new PermissionVO();
        p2.setPermissionId(2L);

        when(roleMapper.getByRoleId(roleId)).thenReturn(role);
        when(permissionMapper.listAllActivePermissions())
                .thenReturn(List.of(p1, p2));

        // when
        List<PermissionVO> permissions =
                rolePermissionService.getRolePermissions(roleId);

        // then
        assertEquals(2, permissions.size());
        verify(permissionMapper).listAllActivePermissions();
        verify(rolePermissionMapper, never()).selectPermissionsByRoleId(any());
    }

    /**
     * 普通角色 → 查角色权限
     */
    @Test
    void getRolePermissions_normalRole_returnRolePermissions() {
        // given
        Long roleId = 2L;

        Role role = new Role();
        role.setRoleId(roleId);
        role.setRoleName("ADMIN");

        PermissionVO p = new PermissionVO();
        p.setPermissionId(10L);

        when(roleMapper.getByRoleId(roleId)).thenReturn(role);
        when(rolePermissionMapper.selectPermissionsByRoleId(roleId))
                .thenReturn(List.of(p));

        // when
        List<PermissionVO> permissions =
                rolePermissionService.getRolePermissions(roleId);

        // then
        assertEquals(1, permissions.size());
        verify(rolePermissionMapper).selectPermissionsByRoleId(roleId);
        verify(permissionMapper, never()).listAllActivePermissions();
    }

}
