package com.ymjrhk.rbac.service.impl;

import com.ymjrhk.rbac.context.LoginUser;
import com.ymjrhk.rbac.context.UserContext;
import com.ymjrhk.rbac.entity.User;
import com.ymjrhk.rbac.exception.*;
import com.ymjrhk.rbac.mapper.RoleMapper;
import com.ymjrhk.rbac.mapper.UserMapper;
import com.ymjrhk.rbac.mapper.UserRoleMapper;
import com.ymjrhk.rbac.vo.RoleVO;
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
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserRoleServiceImplTest {

    @InjectMocks
    private UserRoleServiceImpl userRoleService;

    @Mock
    private UserMapper userMapper;

    @Mock
    private UserRoleMapper userRoleMapper;

    @Mock
    private RoleMapper roleMapper;

    @BeforeEach
    void setUp() {
        // 模拟当前操作人
        UserContext.set(new LoginUser(100L, "admin"));
    }

    @AfterEach
    void tearDown() {
        UserContext.clear();
    }

    // ========================= assignRolesToUser() =========================

    /*
    | 编号 | 场景                     |
    | -- | ---------------------- |
    | T1 | 用户不存在                  |
    | T2 | 用户被禁用                  |
    | T3 | roleIds 非空但含不存在 / 禁用角色 |
    | T4 | 普通管理员分配「超出自己角色范围」      |
    | T5 | roleIds 为空 → 只删不加      |
    | T6 | 普通管理员，合法分配成功           |
    | T7 | 超级管理员，合法分配成功           |
    | T8 | batchInsert 数量不一致      |

    */

    /**
     * 用户不存在
     */
    @Test
    void assignRoles_userNotExist_throwException() {
        // given
        when(userMapper.getByUserId(1L)).thenReturn(null);

        List<Long> roleIds = List.of(1L);

        // when & then
        assertThrows(UserNotExistException.class,
                () -> userRoleService.assignRolesToUser(1L, roleIds));
    }

    /**
     * 用户被禁用
     */
    @Test
    void assignRoles_userDisabled_throwException() {
        // given
        User user = new User();
        user.setUserId(1L);
        user.setStatus(DISABLED);

        when(userMapper.getByUserId(1L)).thenReturn(user);

        List<Long> roleIds = List.of(1L);

        // when & then
        assertThrows(UserForbiddenException.class,
                () -> userRoleService.assignRolesToUser(1L, roleIds));
    }

    /**
     * 提交的角色不存在 / 被禁用
     */
    @Test
    void assignRoles_roleNotExistOrDisabled_throwException() {
        // given
        User user = new User();
        user.setUserId(1L);
        user.setStatus(ENABLED);

        when(userMapper.getByUserId(1L)).thenReturn(user);

        // 前端传 2 个，但数据库只返回 1 个
        when(roleMapper.selectEnabledRoleIds(List.of(1L, 2L)))
                .thenReturn(List.of(1L));

        List<Long> roleIds = List.of(1L, 2L);

        // when & then
        assertThrows(RoleNotExistOrDisabledException.class,
                () -> userRoleService.assignRolesToUser(1L, roleIds));
    }

    /**
     * 普通管理员，分配超出自己权限的角色
     */
    @Test
    void assignRoles_normalAdmin_assignUnownedRole_throwException() {
        // given
        User user = new User();
        user.setUserId(1L);
        user.setStatus(ENABLED);

        when(userMapper.getByUserId(1L)).thenReturn(user);

        when(roleMapper.selectEnabledRoleIds(List.of(1L)))
                .thenReturn(List.of(1L));

        // 当前操作人不是超级管理员
        when(userRoleMapper.userHasRole(100L, SUPER_ADMIN))
                .thenReturn(false);

        // 我自己只拥有 roleId = 2
        when(userRoleMapper.selectRoleIdsByUserIdAndStatus(100L, ENABLED))
                .thenReturn(List.of(2L));

        List<Long> roleIds = List.of(1L);

        // when & then
        assertThrows(AssignmentNotOwnedRoleException.class,
                () -> userRoleService.assignRolesToUser(1L, roleIds));
    }

    /**
     * roleIds 为空 → 只删不加
     */
    @Test
    void assignRoles_emptyRoleIds_onlyDelete() {
        // given
        User user = new User();
        user.setUserId(1L);
        user.setStatus(ENABLED);

        when(userMapper.getByUserId(1L)).thenReturn(user);

        when(userRoleMapper.userHasRole(100L, SUPER_ADMIN))
                .thenReturn(true);

        when(roleMapper.selectAllEnabledRoleIds())
                .thenReturn(List.of(1L, 2L));

        when(userRoleMapper.selectRoleIdsByUserIdAndStatus(1L, ENABLED))
                .thenReturn(List.of(1L, 2L));

        // when
        userRoleService.assignRolesToUser(1L, List.of());

        // then
        verify(userRoleMapper)
                .deleteByUserIdAndRoleIds(1L, Set.of(1L, 2L));

        verify(userRoleMapper, never()).batchInsert(any());
    }

    /**
     * 普通管理员，合法分配成功
     */
    @Test
    void assignRoles_normalAdmin_success() {
        // given
        User user = new User();
        user.setUserId(1L);
        user.setStatus(ENABLED);

        when(userMapper.getByUserId(1L)).thenReturn(user);

        when(roleMapper.selectEnabledRoleIds(List.of(1L)))
                .thenReturn(List.of(1L));

        when(userRoleMapper.userHasRole(100L, SUPER_ADMIN))
                .thenReturn(false);

        when(userRoleMapper.selectRoleIdsByUserIdAndStatus(100L, ENABLED))
                .thenReturn(List.of(1L));

        when(userRoleMapper.selectRoleIdsByUserIdAndStatus(1L, ENABLED))
                .thenReturn(List.of());

        when(userRoleMapper.batchInsert(anyList()))
                .thenReturn(1);

        // when
        userRoleService.assignRolesToUser(1L, List.of(1L));

        // then
        verify(userRoleMapper).batchInsert(argThat(list ->
                list.size() == 1 &&
                        list.getFirst().getUserId().equals(1L) &&
                        list.getFirst().getRoleId().equals(1L) &&
                        list.getFirst().getCreateUserId().equals(100L)
        ));
    }

    /**
     * 超级管理员，合法分配成功
     */
    @Test
    void assignRoles_superAdmin_success() {
        // given
        User user = new User();
        user.setUserId(1L);
        user.setStatus(ENABLED);

        when(userMapper.getByUserId(1L)).thenReturn(user);

        // 前端提交的角色都是启用的
        when(roleMapper.selectEnabledRoleIds(List.of(1L, 2L)))
                .thenReturn(List.of(1L, 2L));

        // 当前操作人是超级管理员
        when(userRoleMapper.userHasRole(100L, SUPER_ADMIN))
                .thenReturn(true);

        // 超级管理员：A = 所有启用角色
        when(roleMapper.selectAllEnabledRoleIds())
                .thenReturn(List.of(1L, 2L, 3L));

        // 被分配用户原有角色
        when(userRoleMapper.selectRoleIdsByUserIdAndStatus(1L, ENABLED))
                .thenReturn(List.of(3L));

        when(userRoleMapper.batchInsert(anyList()))
                .thenReturn(2);

        // when
        userRoleService.assignRolesToUser(1L, List.of(1L, 2L));

        // then
        verify(userRoleMapper).deleteByUserIdAndRoleIds(1L, Set.of(3L));

        verify(userRoleMapper).batchInsert(argThat(list ->
                list.size() == 2 &&
                        list.stream().allMatch(ur ->
                                ur.getUserId().equals(1L) &&
                                        Set.of(1L, 2L).contains(ur.getRoleId()) &&
                                        ur.getCreateUserId().equals(100L)
                        )
        ));
    }

    /**
     * batchInsert 数量不一致 → 回滚异常
     */
    @Test
    void assignRoles_batchInsertFailed_throwException() {
        // given
        User user = new User();
        user.setUserId(1L);
        user.setStatus(ENABLED);

        when(userMapper.getByUserId(1L)).thenReturn(user);

        when(roleMapper.selectEnabledRoleIds(List.of(1L)))
                .thenReturn(List.of(1L));

        when(userRoleMapper.userHasRole(100L, SUPER_ADMIN))
                .thenReturn(false);

        when(userRoleMapper.selectRoleIdsByUserIdAndStatus(100L, ENABLED))
                .thenReturn(List.of(1L));

        when(userRoleMapper.selectRoleIdsByUserIdAndStatus(1L, ENABLED))
                .thenReturn(List.of());

        // 期望插 1 条，但只插入 0 条
        when(userRoleMapper.batchInsert(anyList()))
                .thenReturn(0);

        List<Long> roleIds = List.of(1L);

        // when & then
        assertThrows(AssignmentRoleFailedException.class,
                () -> userRoleService.assignRolesToUser(1L, roleIds));
    }

    // ========================= getUserRoles() =========================

    /**
     * 用户不存在 → 抛异常
     */
    @Test
    void getUserRoles_userNotExist_throwException() {
        // given
        Long userId = 1L;
        when(userMapper.getByUserId(userId)).thenReturn(null);

        // when & then
        assertThrows(UserNotExistException.class,
                () -> userRoleService.getUserRoles(userId));

        // 确认不会继续查角色
        verify(userRoleMapper, never()).selectRolesByUserId(any());
    }

    /**
     * 用户存在 → 正常返回角色列表
     */
    @Test
    void getUserRoles_success_returnRoles() {
        // given
        Long userId = 1L;

        User user = new User();
        user.setUserId(userId);

        RoleVO role1 = new RoleVO();
        role1.setRoleId(10L);
        role1.setRoleName("ADMIN");

        RoleVO role2 = new RoleVO();
        role2.setRoleId(20L);
        role2.setRoleName("USER");

        when(userMapper.getByUserId(userId)).thenReturn(user);
        when(userRoleMapper.selectRolesByUserId(userId))
                .thenReturn(List.of(role1, role2));

        // when
        List<RoleVO> roles = userRoleService.getUserRoles(userId);

        // then
        assertNotNull(roles);
        assertEquals(2, roles.size());
        assertEquals("ADMIN", roles.get(0).getRoleName());
        assertEquals("USER", roles.get(1).getRoleName());

        verify(userRoleMapper).selectRolesByUserId(userId);
    }

    // ========================= userHasRole() =========================
    @Test
    void userHasRole_delegateToMapper_returnTrue() {
        // given
        Long userId = 1L;
        String roleName = "ADMIN";

        when(userRoleMapper.userHasRole(userId, roleName))
                .thenReturn(true);

        // when
        boolean result = userRoleService.userHasRole(userId, roleName);

        // then
        assertTrue(result);
        verify(userRoleMapper).userHasRole(userId, roleName);
    }


}
