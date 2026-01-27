package com.ymjrhk.rbac.service.impl;

import com.github.pagehelper.Page;
import com.ymjrhk.rbac.constant.OperateTypeConstant;
import com.ymjrhk.rbac.constant.PermissionTypeConstant;
import com.ymjrhk.rbac.constant.RoleNameConstant;
import com.ymjrhk.rbac.context.LoginUser;
import com.ymjrhk.rbac.context.UserContext;
import com.ymjrhk.rbac.dto.UserCreateDTO;
import com.ymjrhk.rbac.dto.UserDTO;
import com.ymjrhk.rbac.dto.UserPageQueryDTO;
import com.ymjrhk.rbac.dto.auth.UserAuthInfo;
import com.ymjrhk.rbac.entity.User;
import com.ymjrhk.rbac.exception.*;
import com.ymjrhk.rbac.mapper.PermissionMapper;
import com.ymjrhk.rbac.mapper.UserMapper;
import com.ymjrhk.rbac.result.PageResult;
import com.ymjrhk.rbac.service.UserHistoryService;
import com.ymjrhk.rbac.service.UserRoleService;
import com.ymjrhk.rbac.vo.PermissionVO;
import com.ymjrhk.rbac.vo.UserVO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import java.util.List;

import static com.ymjrhk.rbac.constant.StatusConstant.DISABLED;
import static com.ymjrhk.rbac.constant.StatusConstant.ENABLED;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserHistoryService userHistoryService;

    @Mock
    private PermissionMapper permissionMapper;

    @Mock
    private UserRoleService userRoleService;

    @Mock
    private AntPathMatcher matcher;

    @BeforeEach
    void setUp() {
        // 模拟当前登录用户
        UserContext.set(new LoginUser(100L, "admin"));
    }

    @AfterEach
    void tearDown() {
        UserContext.clear();
    }

    //region create()
    /**
     * 创建成功
     */
    @Test
    void create_success() {
        // given
        UserCreateDTO dto = new UserCreateDTO();
        dto.setUsername("zhangsan");
        dto.setNickname("");
        dto.setEmail("zs@test.com");

        // insert 成功，并模拟 MyBatis 回填 userId
        when(userMapper.insert(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0); // “把 insert 方法接收到的第 0 个参数（User 对象）拿出来”
            u.setUserId(200L); // 模拟数据库生成的 userId
            return 1;
        });

        when(passwordEncoder.encode(anyString())).thenReturn("ENCODED_PASSWORD");
        when(userMapper.updateForCreateUser(any(User.class))).thenReturn(1);

        // when
        Long userId = userService.create(dto);

        // then
        assertEquals(200L, userId);

        // 验证 insert 被调用
        verify(userMapper).insert(any(User.class));

        // 验证 password update 被调用
        verify(userMapper).updateForCreateUser(argThat(user ->
                user.getUserId().equals(200L)
                        && user.getPassword().equals("ENCODED_PASSWORD")
        ));

        // 验证历史表记录
        verify(userHistoryService)
                .record(200L, OperateTypeConstant.CREATE);
    }

    /**
     * insert 失败（row ≠ 1）
     */
    @Test
    void create_insertFail_throwException() {
        // given
        UserCreateDTO dto = new UserCreateDTO();
        dto.setUsername("zhangsan");

        when(userMapper.insert(any(User.class))).thenReturn(0);

        // then
        assertThrows(UserCreateFailedException.class,
                () -> userService.create(dto));

        // insert 失败后，后面的逻辑不应执行
        verify(userMapper, never()).updateForCreateUser(any());
        verify(userHistoryService, never()).record(anyLong(), any());
    }


    /**
     * update 失败
     */
    @Test
    void create_updateFail_throwException() {
        // given
        UserCreateDTO dto = new UserCreateDTO();
        dto.setUsername("zhangsan");

        when(userMapper.insert(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            u.setUserId(300L);
            return 1;
        });

        when(passwordEncoder.encode(anyString())).thenReturn("ENCODED");
        when(userMapper.updateForCreateUser(any(User.class))).thenReturn(0);

        // then
        assertThrows(UserCreateFailedException.class,
                () -> userService.create(dto));

        // update 失败，不应写历史表
        verify(userHistoryService, never())
                .record(anyLong(), any());
    }

    /**
     * nickname 设置为 null
     */
    @Test
    void create_nicknameNull_useUsernameAsNickname() {
        // given
        UserCreateDTO dto = new UserCreateDTO();
        dto.setUsername("zhangsan");
        dto.setNickname(null);

        when(userMapper.insert(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            u.setUserId(1L);
            return 1;
        });
        when(passwordEncoder.encode(anyString())).thenReturn("pwd");
        when(userMapper.updateForCreateUser(any())).thenReturn(1);

        // when
        userService.create(dto);

        // then
        verify(userMapper).insert(argThat(user ->
                user.getNickname().equals("zhangsan")
        ));
    }
    //endregion

    //region pageQuery()
    /**
     * 分页查询成功
     */
    @Test
    void pageQuery_success() {
        // given
        UserPageQueryDTO dto = new UserPageQueryDTO();
        dto.setPageNum(1);
        dto.setPageSize(10);

        // 构造 Page<User>
        Page<User> mockPage = new Page<>();
        mockPage.setTotal(2);

        User u1 = new User();
        u1.setUserId(1L);
        u1.setUsername("zhangsan");

        User u2 = new User();
        u2.setUserId(2L);
        u2.setUsername("lisi");

        mockPage.add(u1);
        mockPage.add(u2);

        when(userMapper.pageQuery(dto)).thenReturn(mockPage);

        // when
        PageResult result = userService.pageQuery(dto);

        // then
        assertNotNull(result);
        assertEquals(2, result.getTotal());

        List<UserVO> records = result.getRecords();
        assertEquals(2, records.size());
        assertEquals("zhangsan", records.get(0).getUsername());
        assertEquals("lisi", records.get(1).getUsername());

        // 验证 mapper 被调用
        verify(userMapper).pageQuery(dto);
    }

    /**
     * pageNum / pageSize 为空时兜底
     */
    @Test
    void pageQuery_pageParamNull_useDefault() {
        // given
        UserPageQueryDTO dto = new UserPageQueryDTO();
        // 不设置 pageNum / pageSize

        Page<User> emptyPage = new Page<>();
        emptyPage.setTotal(0);

        when(userMapper.pageQuery(dto)).thenReturn(emptyPage);

        // when
        PageResult result = userService.pageQuery(dto);

        // then
        assertEquals(0, result.getTotal());
        assertTrue(result.getRecords().isEmpty());

        // 关键：验证 normalizePage 生效
        assertNotNull(dto.getPageNum());
        assertNotNull(dto.getPageSize());

        verify(userMapper).pageQuery(dto);
    }

    /**
     * pageNum < 1（非法页码）
     */
    @Test
    void pageQuery_pageNumLessThanOne_useDefault() {
        // given
        UserPageQueryDTO dto = new UserPageQueryDTO();
        dto.setPageNum(0);      // 非法
        dto.setPageSize(10);    // 合法

        Page<User> emptyPage = new Page<>();
        when(userMapper.pageQuery(dto)).thenReturn(emptyPage);

        // when
        userService.pageQuery(dto);

        // then
        assertEquals(1, dto.getPageNum());   // DEFAULT_PAGE_NUM
        assertEquals(10, dto.getPageSize());
    }

    /**
     * pageSize <= 0（非法 pageSize）
     */
    @Test
    void pageQuery_pageSizeLessThanOrEqualZero_useDefault() {
        // given
        UserPageQueryDTO dto = new UserPageQueryDTO();
        dto.setPageNum(1);
        dto.setPageSize(0); // 非法

        Page<User> emptyPage = new Page<>();
        when(userMapper.pageQuery(dto)).thenReturn(emptyPage);

        // when
        userService.pageQuery(dto);

        // then
        assertEquals(1, dto.getPageNum());
        assertEquals(10, dto.getPageSize()); // DEFAULT_PAGE_SIZE
    }

    /**
     * pageSize > MAX_PAGE_SIZE（超上限）
     */
    @Test
    void pageQuery_pageSizeGreaterThanMax_useMax() {
        // given
        UserPageQueryDTO dto = new UserPageQueryDTO();
        dto.setPageNum(1);
        dto.setPageSize(1000); // 超大

        Page<User> emptyPage = new Page<>();
        when(userMapper.pageQuery(dto)).thenReturn(emptyPage);

        // when
        userService.pageQuery(dto);

        // then
        assertEquals(1, dto.getPageNum());
        assertEquals(100, dto.getPageSize()); // MAX_PAGE_SIZE
    }
    //endregion

    //region getByUserId
    /**
     * 用户存在（成功路径）
     */
    @Test
    void getByUserId_success() {
        // given
        Long userId = 1L;

        User user = new User();
        user.setUserId(userId);
        user.setUsername("zhangsan");
        user.setNickname("张三");

        when(userMapper.getByUserId(userId)).thenReturn(user);

        // when
        UserVO result = userService.getByUserId(userId);

        // then
        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        assertEquals("zhangsan", result.getUsername());
        assertEquals("张三", result.getNickname());

        verify(userMapper).getByUserId(userId);
    }

    /**
     * 用户不存在（异常路径）
     */
    @Test
    void getByUserId_userNotExist_throwException() {
        // given
        Long userId = 99L;

        when(userMapper.getByUserId(userId)).thenReturn(null);

        // then
        assertThrows(UserNotExistException.class,
                () -> userService.getByUserId(userId));

        verify(userMapper).getByUserId(userId);
    }
    //endregion

    //region update
    /**
     * 用户不存在
     */
    @Test
    void update_userNotExist_throwException() {
        // given
        Long userId = 1L;
        UserDTO dto = new UserDTO();

        when(userMapper.getByUserId(userId)).thenReturn(null);

        // then
        assertThrows(UserNotExistException.class,
                () -> userService.update(userId, dto));

        verify(userMapper).getByUserId(userId);
        verify(userMapper, never()).update(any());
        verify(userHistoryService, never()).record(anyLong(), any());
    }

    /**
     * 用户被禁用
     */
    @Test
    void update_userDisabled_throwException() {
        // given
        Long userId = 1L;

        User dbUser = new User();
        dbUser.setUserId(userId);
        dbUser.setStatus(DISABLED);

        when(userMapper.getByUserId(userId)).thenReturn(dbUser);

        // then
        assertThrows(UserForbiddenException.class,
                () -> userService.update(userId, new UserDTO()));

        verify(userMapper, never()).update(any());
        verify(userHistoryService, never()).record(anyLong(), any());
    }

    /**
     * 正常更新（username 未改变）
     */
    @Test
    void update_success_usernameNotChanged() {
        // given
        Long userId = 1L;

        User dbUser = new User();
        dbUser.setUserId(userId);
        dbUser.setUsername("zhangsan");
        dbUser.setStatus(1);
        dbUser.setAuthVersion(3);

        UserDTO dto = new UserDTO();
        dto.setUsername("zhangsan"); // 未改变
        dto.setVersion(3);
        dto.setSecretToken("old-token");

        when(userMapper.getByUserId(userId)).thenReturn(dbUser);
        when(userMapper.update(any(User.class))).thenReturn(1);

        // when
        userService.update(userId, dto);

        // then
        verify(userMapper).update(argThat(user ->
                user.getUserId().equals(userId)
                        && user.getAuthVersion() == null   // username 没变，不触发 auth_version
                        && user.getVersion().equals(3)
                        && user.getSecretToken().equals("old-token")
                        && user.getUpdateUserId().equals(100L)
        ));

        verify(userHistoryService)
                .record(userId, OperateTypeConstant.UPDATE);
    }

    /**
     * 正常更新（username 改变 → auth_version + 1）
     */
    @Test
    void update_success_usernameChanged_authVersionIncrease() {
        // given
        Long userId = 1L;

        User dbUser = new User();
        dbUser.setUserId(userId);
        dbUser.setUsername("zhangsan");
        dbUser.setStatus(1);
        dbUser.setAuthVersion(5);

        UserDTO dto = new UserDTO();
        dto.setUsername("lisi"); // 修改了 username
        dto.setVersion(5);
        dto.setSecretToken("old-token");

        when(userMapper.getByUserId(userId)).thenReturn(dbUser);
        when(userMapper.update(any(User.class))).thenReturn(1);

        // when
        userService.update(userId, dto);

        // then
        verify(userMapper).update(argThat(user ->
                user.getAuthVersion() != null // 触发 xml 中 auth_version + 1
        ));

        verify(userHistoryService)
                .record(userId, OperateTypeConstant.UPDATE);
    }

    /**
     * 更新失败（乐观锁失败）
     */
    @Test
    void update_updateFail_throwException() {
        // given
        Long userId = 1L;

        User dbUser = new User();
        dbUser.setUserId(userId);
        dbUser.setUsername("zhangsan");
        dbUser.setStatus(1);

        UserDTO dto = new UserDTO();
        dto.setVersion(1);
        dto.setSecretToken("old-token");

        when(userMapper.getByUserId(userId)).thenReturn(dbUser);
        when(userMapper.update(any(User.class))).thenReturn(0);

        // then
        assertThrows(UpdateFailedException.class,
                () -> userService.update(userId, dto));

        verify(userHistoryService, never())
                .record(anyLong(), any());
    }
    //endregion

    //region changeStatus
    /**
     * 用户不存在
     */
    @Test
    void changeStatus_userNotExist_throwException() {
        // given
        Long userId = 1L;

        when(userMapper.getByUserId(userId)).thenReturn(null);

        // then
        assertThrows(UserNotExistException.class,
                () -> userService.changeStatus(userId, DISABLED));

        verify(userMapper).getByUserId(userId);
        verify(userMapper, never()).update(any());
        verify(userHistoryService, never()).record(anyLong(), any());
    }

    /**
     * 状态未改变
     */
    @Test
    void changeStatus_statusNotChange_throwException() {
        // given
        Long userId = 1L;

        User dbUser = new User();
        dbUser.setUserId(userId);
        dbUser.setStatus(ENABLED);
        dbUser.setVersion(1);
        dbUser.setSecretToken("old-token");

        when(userMapper.getByUserId(userId)).thenReturn(dbUser);

        // then
        assertThrows(StatusNotChangeException.class,
                () -> userService.changeStatus(userId, ENABLED));

        verify(userMapper, never()).update(any());
        verify(userHistoryService, never()).record(anyLong(), any());
    }

    /**
     * 启用 → 禁用（auth_version 要加）
     */
    @Test
    void changeStatus_enabledToDisabled_authVersionIncrease() {
        // given
        Long userId = 1L;

        User dbUser = new User();
        dbUser.setUserId(userId);
        dbUser.setStatus(ENABLED);
        dbUser.setAuthVersion(5);
        dbUser.setVersion(2);
        dbUser.setSecretToken("old-token");

        when(userMapper.getByUserId(userId)).thenReturn(dbUser);
        when(userMapper.update(any(User.class))).thenReturn(1);

        // when
        userService.changeStatus(userId, DISABLED);

        // then
        verify(userMapper).update(argThat(user ->
                user.getStatus().equals(DISABLED)
                        && user.getAuthVersion() != null   // 触发 auth_version + 1
                        && user.getUpdateUserId().equals(100L)
        ));

        verify(userHistoryService)
                .record(userId, OperateTypeConstant.UPDATE);
    }
    //endregion

    //region resetPassword
    /**
     * 用户不存在
     */
    @Test
    void resetPassword_userNotExist_throwException() {
        // given
        Long userId = 1L;

        when(userMapper.getByUserId(userId)).thenReturn(null);

        // then
        assertThrows(UserNotExistException.class,
                () -> userService.resetPassword(userId));

        verify(userMapper).getByUserId(userId);
        verify(userMapper, never()).update(any());
        verify(userHistoryService, never()).record(anyLong(), any());
    }

    /**
     * 用户被禁用
     */
    @Test
    void resetPassword_userDisabled_throwException() {
        // given
        Long userId = 1L;

        User dbUser = new User();
        dbUser.setUserId(userId);
        dbUser.setStatus(DISABLED);

        when(userMapper.getByUserId(userId)).thenReturn(dbUser);

        // then
        assertThrows(UserForbiddenException.class,
                () -> userService.resetPassword(userId));

        verify(userMapper, never()).update(any());
        verify(userHistoryService, never()).record(anyLong(), any());
    }

    /**
     * 正常重置密码
     */
    @Test
    void resetPassword_success() {
        // given
        Long userId = 1L;

        User dbUser = new User();
        dbUser.setUserId(userId);
        dbUser.setStatus(ENABLED);
        dbUser.setVersion(3);
        dbUser.setSecretToken("old-token");
        dbUser.setAuthVersion(7);

        when(userMapper.getByUserId(userId)).thenReturn(dbUser);
        when(passwordEncoder.encode(anyString())).thenReturn("ENCODED_PASSWORD");
        when(userMapper.update(any(User.class))).thenReturn(1);

        // when
        userService.resetPassword(userId);

        // then
        verify(passwordEncoder)
                .encode(argThat(s -> s.toString().endsWith("#" + userId)));

        verify(userMapper).update(argThat(user ->
                user.getUserId().equals(userId)
                        && user.getPassword().equals("ENCODED_PASSWORD")
                        && user.getVersion().equals(3)
                        && user.getSecretToken().equals("old-token")
                        && user.getAuthVersion() != null      // 触发 auth_version + 1
                        && user.getUpdateUserId().equals(100L)
        ));

        verify(userHistoryService)
                .record(userId, OperateTypeConstant.UPDATE);
    }

    /**
     * update 失败（乐观锁 / 并发失败）
     */
    @Test
    void resetPassword_updateFail_throwException() {
        // given
        Long userId = 1L;

        User dbUser = new User();
        dbUser.setUserId(userId);
        dbUser.setStatus(ENABLED);
        dbUser.setVersion(1);
        dbUser.setSecretToken("old-token");
        dbUser.setAuthVersion(2);

        when(userMapper.getByUserId(userId)).thenReturn(dbUser);
        when(passwordEncoder.encode(anyString())).thenReturn("ENCODED");
        when(userMapper.update(any(User.class))).thenReturn(0);

        // then
        assertThrows(UpdateFailedException.class,
                () -> userService.resetPassword(userId));

        verify(userHistoryService, never())
                .record(anyLong(), any());
    }
    //endregion

    //region getUserPermissions
    /**
     * 用户不存在
     */
    @Test
    void getUserPermissions_userNotExist_throwException() {
        // given
        Long userId = 1L;

        when(userMapper.getByUserId(userId)).thenReturn(null);

        // then
        assertThrows(UserNotExistException.class,
                () -> userService.getUserPermissions(userId));

        verify(userMapper).getByUserId(userId);
        verify(permissionMapper, never()).listAllActivePermissions();
        verify(userMapper, never()).selectPermissionsByUserId(anyLong());
    }

    /**
     * 超级管理员
     */
    @Test
    void getUserPermissions_superAdmin_returnAllPermissions() {
        // given
        Long userId = 1L;

        User user = new User();
        user.setUserId(userId);

        PermissionVO p1 = new PermissionVO();
        p1.setPermissionId(1L);
        p1.setPermissionName("USER:VIEW");

        PermissionVO p2 = new PermissionVO();
        p2.setPermissionId(2L);
        p2.setPermissionName("USER:CREATE");

        List<PermissionVO> allPermissions = List.of(p1, p2);

        when(userMapper.getByUserId(userId)).thenReturn(user);
        when(userRoleService.userHasRole(userId, RoleNameConstant.SUPER_ADMIN))
                .thenReturn(true);
        when(permissionMapper.listAllActivePermissions())
                .thenReturn(allPermissions);

        // when
        List<PermissionVO> result =
                userService.getUserPermissions(userId);

        // then
        assertEquals(2, result.size());
        assertEquals("USER:VIEW", result.getFirst().getPermissionName());

        verify(permissionMapper).listAllActivePermissions();
        verify(userMapper, never()).selectPermissionsByUserId(anyLong());
    }

    /**
     * 普通用户（非超级管理员）
     */
    @Test
    void getUserPermissions_normalUser_returnUserPermissions() {
        // given
        Long userId = 2L;

        User user = new User();
        user.setUserId(userId);

        PermissionVO p1 = new PermissionVO();
        p1.setPermissionId(10L);
        p1.setPermissionName("ROLE:ASSIGN");

        PermissionVO p2 = new PermissionVO();
        p2.setPermissionId(11L);
        p2.setPermissionName("USER:UPDATE");

        List<PermissionVO> userPermissions = List.of(p1, p2);

        when(userMapper.getByUserId(userId)).thenReturn(user);
        when(userRoleService.userHasRole(userId, RoleNameConstant.SUPER_ADMIN))
                .thenReturn(false);
        when(userMapper.selectPermissionsByUserId(userId))
                .thenReturn(userPermissions);

        // when
        List<PermissionVO> result =
                userService.getUserPermissions(userId);

        // then
        assertEquals(2, result.size());
        assertEquals("ROLE:ASSIGN", result.getFirst().getPermissionName());

        verify(userMapper).selectPermissionsByUserId(userId);
        verify(permissionMapper, never()).listAllActivePermissions();
    }
    //endregion

    //region hasPermission
    /**
     * 超级管理员直接放行
     */
    @Test
    void hasPermission_superAdmin_returnTrue() {
        // given
        Long userId = 1L;

        when(userRoleService.userHasRole(userId, RoleNameConstant.SUPER_ADMIN))
                .thenReturn(true);

        // when
        boolean result = userService.hasPermission(
                userId, "/api/users/1", "GET");

        // then
        assertTrue(result);

        // 超管不会查用户、不会查权限
        verify(userMapper, never()).selectPermissionsByUserId(anyLong());
        verify(matcher, never()).match(anyString(), anyString());
    }

    /**
     * 普通用户 + 权限匹配成功
     */
    @Test
    void hasPermission_normalUser_matchSuccess_returnTrue() {
        // given
        Long userId = 2L;

        User user = new User();
        user.setUserId(userId);

        PermissionVO p = new PermissionVO();
        p.setPermissionName("USER:VIEW");
        p.setType(PermissionTypeConstant.ACTION);
        p.setPath("/api/users/**");
        p.setMethod("GET");

        when(userRoleService.userHasRole(userId, RoleNameConstant.SUPER_ADMIN))
                .thenReturn(false);
        when(userMapper.getByUserId(userId)).thenReturn(user);
        when(userMapper.selectPermissionsByUserId(userId))
                .thenReturn(List.of(p));

        // when
        boolean result = userService.hasPermission(
                userId, "/api/users/1", "GET");

        // then
        assertTrue(result);
    }

    /**
     * 普通用户无任何权限
     */
    @Test
    void hasPermission_normalUser_noPermission_returnFalse() {
        // given
        Long userId = 3L;

        User user = new User();
        user.setUserId(userId);

        when(userRoleService.userHasRole(userId, RoleNameConstant.SUPER_ADMIN))
                .thenReturn(false);
        when(userMapper.getByUserId(userId)).thenReturn(user);
        when(userMapper.selectPermissionsByUserId(userId))
                .thenReturn(List.of());

        // when
        boolean result = userService.hasPermission(
                userId, "/api/users/1", "GET");

        // then
        assertFalse(result);
    }

    /**
     * 权限类型不是 ACTION（应跳过）
     */
    @Test
    void hasPermission_permissionTypeNotAction_skipAndReturnFalse() {
        // given
        Long userId = 4L;

        User user = new User();
        user.setUserId(userId);

        PermissionVO p = new PermissionVO();
        p.setType(PermissionTypeConstant.MODULE); // 非 ACTION
        p.setPath("/api/users/**");
        p.setMethod("GET");

        when(userRoleService.userHasRole(userId, RoleNameConstant.SUPER_ADMIN))
                .thenReturn(false);
        when(userMapper.getByUserId(userId)).thenReturn(user);
        when(userMapper.selectPermissionsByUserId(userId))
                .thenReturn(List.of(p));

        // when
        boolean result = userService.hasPermission(
                userId, "/api/users/1", "GET");

        // then
        assertFalse(result);
    }

    /**
     * path / method 不匹配
     */
    @Test
    void hasPermission_pathOrMethodNotMatch_returnFalse() {
        // given
        Long userId = 5L;

        User user = new User();
        user.setUserId(userId);

        PermissionVO p = new PermissionVO();
        p.setType(PermissionTypeConstant.ACTION);
        p.setPath("/api/admin/**");
        p.setMethod("POST");

        when(userRoleService.userHasRole(userId, RoleNameConstant.SUPER_ADMIN))
                .thenReturn(false);
        when(userMapper.getByUserId(userId)).thenReturn(user);
        when(userMapper.selectPermissionsByUserId(userId))
                .thenReturn(List.of(p));

        // when
        boolean result = userService.hasPermission(
                userId, "/api/users/1", "GET");

        // then
        assertFalse(result);
    }

    /**
     * 普通用户不存在
     */
    @Test
    void hasPermission_userNotExist_throwException() {
        // given
        Long userId = 6L;

        when(userRoleService.userHasRole(userId, RoleNameConstant.SUPER_ADMIN))
                .thenReturn(false);
        when(userMapper.getByUserId(userId)).thenReturn(null);

        // then
        assertThrows(UserNotExistException.class,
                () -> userService.hasPermission(
                        userId, "/api/users/1", "GET"));
    }
    //endregion

    //region getUserAuthInfo

    /**
     * 用户存在（成功路径）
     */
    @Test
    void getUserAuthInfo_success() {
        // given
        Long userId = 1L;

        UserAuthInfo authInfo = new UserAuthInfo();
        authInfo.setUserId(userId);
        authInfo.setUsername("zhangsan");
        authInfo.setAuthVersion(3);

        when(userMapper.getUserAuthInfo(userId))
                .thenReturn(authInfo);

        // when
        UserAuthInfo result = userService.getUserAuthInfo(userId);

        // then
        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        assertEquals("zhangsan", result.getUsername());
        assertEquals(3, result.getAuthVersion());

        verify(userMapper).getUserAuthInfo(userId);
    }

    /**
     * 用户不存在（异常路径）
     */
    @Test
    void getUserAuthInfo_userNotExist_throwException() {
        // given
        Long userId = 99L;

        when(userMapper.getUserAuthInfo(userId))
                .thenReturn(null);

        // then
        assertThrows(UserNotExistException.class,
                () -> userService.getUserAuthInfo(userId));

        verify(userMapper).getUserAuthInfo(userId);
    }
    //endregion

    //region incrementAuthVersion
    /**
     * 更新成功（正常路径）
     */
    @Test
    void incrementAuthVersion_success() {
        // given
        Long userId = 1L;

        when(userMapper.incrementAuthVersion(userId))
                .thenReturn(1);

        // when
        userService.incrementAuthVersion(userId);

        // then
        verify(userMapper).incrementAuthVersion(userId);
    }

    /**
     * 用户不存在（异常路径）
     */
    @Test
    void incrementAuthVersion_userNotExist_throwException() {
        // given
        Long userId = 99L;

        when(userMapper.incrementAuthVersion(userId))
                .thenReturn(0);

        // then
        assertThrows(UserNotExistException.class,
                () -> userService.incrementAuthVersion(userId));

        verify(userMapper).incrementAuthVersion(userId);
    }
    //endregion

}
