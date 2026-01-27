package com.ymjrhk.rbac.service.impl;

import com.ymjrhk.rbac.constant.OperateTypeConstant;
import com.ymjrhk.rbac.constant.StatusConstant;
import com.ymjrhk.rbac.context.LoginUser;
import com.ymjrhk.rbac.context.UserContext;
import com.ymjrhk.rbac.dto.MePasswordUpdateDTO;
import com.ymjrhk.rbac.dto.MeUpdateDTO;
import com.ymjrhk.rbac.entity.User;
import com.ymjrhk.rbac.exception.PasswordErrorException;
import com.ymjrhk.rbac.exception.UpdateFailedException;
import com.ymjrhk.rbac.exception.UserForbiddenException;
import com.ymjrhk.rbac.exception.UserNotExistException;
import com.ymjrhk.rbac.mapper.MeMapper;
import com.ymjrhk.rbac.mapper.PermissionMapper;
import com.ymjrhk.rbac.mapper.UserMapper;
import com.ymjrhk.rbac.service.UserHistoryService;
import com.ymjrhk.rbac.vo.MePermissionVO;
import com.ymjrhk.rbac.vo.MeRoleVO;
import com.ymjrhk.rbac.vo.MeViewVO;
import com.ymjrhk.rbac.vo.PermissionVO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

import static com.ymjrhk.rbac.constant.RoleNameConstant.SUPER_ADMIN;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MeServiceImplTest {

    @InjectMocks
    private MeServiceImpl meService;

    @Mock
    private MeMapper meMapper;

    @Mock
    private UserMapper userMapper; // 构造器需要，query() 本身没用到

    @Mock
    private UserHistoryService userHistoryService; // 构造器需要

    @Mock
    private PasswordEncoder passwordEncoder; // 构造器需要

    @Mock
    private PermissionMapper permissionMapper;

    @AfterEach
    void tearDown() {
        UserContext.clear();
    }

    // ========================= query() =========================

    /**
     * 普通用户（没有 SUPER_ADMIN）
     */
    @Test
    void query_normalUser_returnOwnPermissions() {
        // given
        Long userId = 1L;
        UserContext.set(new LoginUser(userId, "zhangsan"));

        MeViewVO meViewVO = new MeViewVO();
        meViewVO.setUserId(userId);
        meViewVO.setUsername("zhangsan");

        MeRoleVO role = new MeRoleVO();
        role.setRoleName("USER");

        MePermissionVO permission = new MePermissionVO();
        permission.setPermissionName("USER:VIEW");

        when(meMapper.getByUserId(userId))
                .thenReturn(meViewVO);
        when(meMapper.selectRolesByUserId(userId))
                .thenReturn(List.of(role));
        when(meMapper.selectPermissionsByUserId(userId))
                .thenReturn(List.of(permission));

        // when
        MeViewVO result = meService.query();

        // then
        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        assertEquals(1, result.getRoles().size());
        assertEquals("USER", result.getRoles().get(0).getRoleName());

        assertEquals(1, result.getPermissions().size());
        assertEquals("USER:VIEW", result.getPermissions().get(0).getPermissionName());

        verify(permissionMapper, never()).listAllActivePermissions();
    }

    /**
     * 超级管理员用户
     */
    @Test
    void query_superAdmin_returnAllPermissions() {
        // given
        Long userId = 1L;
        UserContext.set(new LoginUser(userId, "admin"));

        MeViewVO meViewVO = new MeViewVO();
        meViewVO.setUserId(userId);
        meViewVO.setUsername("admin");

        MeRoleVO superAdminRole = new MeRoleVO();
        superAdminRole.setRoleName(SUPER_ADMIN);

        PermissionVO permissionVO = new PermissionVO();
        permissionVO.setPermissionName("USER:VIEW");

        when(meMapper.getByUserId(userId))
                .thenReturn(meViewVO);
        when(meMapper.selectRolesByUserId(userId))
                .thenReturn(List.of(superAdminRole));
        when(permissionMapper.listAllActivePermissions())
                .thenReturn(List.of(permissionVO));

        // when
        MeViewVO result = meService.query();

        // then
        assertNotNull(result);
        assertEquals(1, result.getPermissions().size());
        assertEquals("USER:VIEW", result.getPermissions().get(0).getPermissionName());

        verify(permissionMapper).listAllActivePermissions();
        verify(meMapper, never()).selectPermissionsByUserId(anyLong());
    }

    /**
     * userId 查不到（兜底分支）
     */
    @Test
    void query_userNotExist_throwException() {
        // given
        Long userId = 99L;
        UserContext.set(new LoginUser(userId, "ghost"));

        when(meMapper.getByUserId(userId))
                .thenReturn(null);

        // then
        assertThrows(UserNotExistException.class,
                () -> meService.query());
    }

    // ========================= update() =========================

    /**
     * 正常修改成功
     */
    @Test
    void update_success() {
        // given
        Long userId = 1L;
        UserContext.set(new LoginUser(userId, "zhangsan"));

        MeUpdateDTO dto = new MeUpdateDTO();
        dto.setNickname("新昵称");
        dto.setEmail("new@test.com");

        User dbUser = new User();
        dbUser.setUserId(userId);
        dbUser.setStatus(StatusConstant.ENABLED);
        dbUser.setVersion(3);
        dbUser.setSecretToken("old-token");

        when(userMapper.getByUserId(userId))
                .thenReturn(dbUser);
        when(userMapper.update(any(User.class)))
                .thenReturn(1);

        // when
        meService.update(dto);

        // then
        verify(userMapper).update(argThat(user ->
                user.getUserId().equals(userId)
                        && user.getNickname().equals("新昵称")
                        && user.getEmail().equals("new@test.com")
                        && user.getVersion().equals(3)
                        && user.getSecretToken().equals("old-token")
                        && user.getUpdateUserId().equals(userId)
                        && user.getNewSecretToken() != null
        ));

        verify(userHistoryService)
                .record(userId, OperateTypeConstant.UPDATE);
    }

    /**
     * 用户不存在
     */
    @Test
    void update_userNotExist_throwException() {
        // given
        Long userId = 1L;
        UserContext.set(new LoginUser(userId, "ghost"));

        MeUpdateDTO dto = new MeUpdateDTO();

        when(userMapper.getByUserId(userId))
                .thenReturn(null);

        // then
        assertThrows(UserNotExistException.class,
                () -> meService.update(dto));

        verify(userMapper, never()).update(any());
        verify(userHistoryService, never()).record(anyLong(), anyInt());
    }

    /**
     * 用户被禁用
     */
    @Test
    void update_userDisabled_throwException() {
        // given
        Long userId = 1L;
        UserContext.set(new LoginUser(userId, "zhangsan"));

        MeUpdateDTO dto = new MeUpdateDTO();

        User dbUser = new User();
        dbUser.setUserId(userId);
        dbUser.setStatus(StatusConstant.DISABLED);

        when(userMapper.getByUserId(userId))
                .thenReturn(dbUser);

        // then
        assertThrows(UserForbiddenException.class,
                () -> meService.update(dto));

        verify(userMapper, never()).update(any());
    }

    /**
     * 更新失败（乐观锁失败）
     */
    @Test
    void update_updateFail_throwException() {
        // given
        Long userId = 1L;
        UserContext.set(new LoginUser(userId, "zhangsan"));

        MeUpdateDTO dto = new MeUpdateDTO();
        dto.setNickname("新昵称");

        User dbUser = new User();
        dbUser.setUserId(userId);
        dbUser.setStatus(StatusConstant.ENABLED);
        dbUser.setVersion(1);
        dbUser.setSecretToken("token");

        when(userMapper.getByUserId(userId))
                .thenReturn(dbUser);
        when(userMapper.update(any(User.class)))
                .thenReturn(0); // 模拟更新失败

        // then
        assertThrows(UpdateFailedException.class,
                () -> meService.update(dto));

        verify(userHistoryService, never())
                .record(anyLong(), anyInt());
    }

    // ========================= changePassword() =========================

    /**
     * 正常修改密码
     */
    @Test
    void changePassword_success() {
        // given
        Long userId = 1L;
        UserContext.set(new LoginUser(userId, "zhangsan"));

        MePasswordUpdateDTO dto = new MePasswordUpdateDTO();
        dto.setOldPassword("old123");
        dto.setNewPassword("new123");

        User dbUser = new User();
        dbUser.setUserId(userId);
        dbUser.setPassword("ENCODED_OLD");
        dbUser.setStatus(StatusConstant.ENABLED);
        dbUser.setVersion(2);
        dbUser.setSecretToken("old-token");
        dbUser.setAuthVersion(5);

        when(userMapper.getByUserId(userId))
                .thenReturn(dbUser);
        when(passwordEncoder.matches("old123#1", "ENCODED_OLD"))
                .thenReturn(true);
        when(passwordEncoder.encode("new123#1"))
                .thenReturn("ENCODED_NEW");
        when(userMapper.update(any(User.class)))
                .thenReturn(1);

        // when
        meService.changePassword(dto);

        // then
        verify(userMapper).update(argThat(user ->
                user.getUserId().equals(userId)
                        && user.getPassword().equals("ENCODED_NEW")
                        && user.getVersion().equals(2)
                        && user.getSecretToken().equals("old-token")
                        && user.getAuthVersion().equals(5) // 触发 auth_version + 1
                        && user.getUpdateUserId().equals(userId)
                        && user.getNewSecretToken() != null
        ));

        verify(userHistoryService)
                .record(userId, OperateTypeConstant.UPDATE);
    }

    /**
     * 用户不存在
     */
    @Test
    void changePassword_userNotExist_throwException() {
        // given
        Long userId = 1L;
        UserContext.set(new LoginUser(userId, "ghost"));

        MePasswordUpdateDTO dto = new MePasswordUpdateDTO();
        dto.setOldPassword("old");
        dto.setNewPassword("new");

        when(userMapper.getByUserId(userId))
                .thenReturn(null);

        // then
        assertThrows(UserNotExistException.class,
                () -> meService.changePassword(dto));

        verify(userMapper, never()).update(any());
        verify(userHistoryService, never()).record(anyLong(), anyInt());
    }

    /**
     * 原密码错误
     */
    @Test
    void changePassword_oldPasswordWrong_throwException() {
        // given
        Long userId = 1L;
        UserContext.set(new LoginUser(userId, "zhangsan"));

        MePasswordUpdateDTO dto = new MePasswordUpdateDTO();
        dto.setOldPassword("wrong");
        dto.setNewPassword("new123");

        User dbUser = new User();
        dbUser.setUserId(userId);
        dbUser.setPassword("ENCODED_OLD");
        dbUser.setStatus(StatusConstant.ENABLED);

        when(userMapper.getByUserId(userId))
                .thenReturn(dbUser);
        when(passwordEncoder.matches("wrong#1", "ENCODED_OLD"))
                .thenReturn(false);

        // then
        assertThrows(PasswordErrorException.class,
                () -> meService.changePassword(dto));

        verify(userMapper, never()).update(any());
    }

    /**
     * 用户被禁用
     */
    @Test
    void changePassword_userDisabled_throwException() {
        // given
        Long userId = 1L;
        UserContext.set(new LoginUser(userId, "zhangsan"));

        MePasswordUpdateDTO dto = new MePasswordUpdateDTO();
        dto.setOldPassword("old");
        dto.setNewPassword("new");

        User dbUser = new User();
        dbUser.setUserId(userId);
        dbUser.setPassword("ENCODED_OLD");
        dbUser.setStatus(StatusConstant.DISABLED);

        when(userMapper.getByUserId(userId))
                .thenReturn(dbUser);
        when(passwordEncoder.matches("old#1", "ENCODED_OLD"))
                .thenReturn(true);

        // then
        assertThrows(UserForbiddenException.class,
                () -> meService.changePassword(dto));
    }

    /**
     * 更新失败（乐观锁失败）
     */
    @Test
    void changePassword_updateFail_throwException() {
        // given
        Long userId = 1L;
        UserContext.set(new LoginUser(userId, "zhangsan"));

        MePasswordUpdateDTO dto = new MePasswordUpdateDTO();
        dto.setOldPassword("old");
        dto.setNewPassword("new");

        User dbUser = new User();
        dbUser.setUserId(userId);
        dbUser.setPassword("ENCODED_OLD");
        dbUser.setStatus(StatusConstant.ENABLED);
        dbUser.setVersion(1);
        dbUser.setSecretToken("token");
        dbUser.setAuthVersion(3);

        when(userMapper.getByUserId(userId))
                .thenReturn(dbUser);
        when(passwordEncoder.matches("old#1", "ENCODED_OLD"))
                .thenReturn(true);
        when(passwordEncoder.encode("new#1"))
                .thenReturn("ENCODED_NEW");
        when(userMapper.update(any(User.class)))
                .thenReturn(0);

        // then
        assertThrows(UpdateFailedException.class,
                () -> meService.changePassword(dto));

        verify(userHistoryService, never())
                .record(anyLong(), anyInt());
    }










}
