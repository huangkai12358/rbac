package com.ymjrhk.rbac.service.impl;

import com.github.pagehelper.Page;
import com.ymjrhk.rbac.constant.OperateTypeConstant;
import com.ymjrhk.rbac.constant.StatusConstant;
import com.ymjrhk.rbac.context.LoginUser;
import com.ymjrhk.rbac.context.UserContext;
import com.ymjrhk.rbac.dto.RoleCreateDTO;
import com.ymjrhk.rbac.dto.RoleDTO;
import com.ymjrhk.rbac.dto.RolePageQueryDTO;
import com.ymjrhk.rbac.entity.Role;
import com.ymjrhk.rbac.exception.*;
import com.ymjrhk.rbac.mapper.RoleMapper;
import com.ymjrhk.rbac.result.PageResult;
import com.ymjrhk.rbac.service.RoleHistoryService;
import com.ymjrhk.rbac.vo.RoleVO;
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
class RoleServiceImplTest {

    @InjectMocks
    private RoleServiceImpl roleService;

    @Mock
    private RoleMapper roleMapper;

    @Mock
    private RoleHistoryService roleHistoryService;

    @BeforeEach
    void setUp() {
        // 模拟当前登录用户
        UserContext.set(new LoginUser(100L, "admin"));
    }

    @AfterEach
    void tearDown() {
        UserContext.clear();
    }

    // ========================= create() =========================

    /**
     * 创建角色成功
     */
    @Test
    void create_success() {
        // given
        RoleCreateDTO dto = new RoleCreateDTO();
        dto.setRoleName("ADMIN");
        dto.setRoleDisplayName("管理员");

        // 模拟 insert 成功，并回填 roleId
        when(roleMapper.insert(any(Role.class))).thenAnswer(invocation -> {
            Role role = invocation.getArgument(0);
            role.setRoleId(10L); // 模拟数据库生成的主键
            return 1;
        });

        // when
        Long roleId = roleService.create(dto);

        // then
        assertEquals(10L, roleId);

        verify(roleMapper).insert(argThat(role ->
                role.getRoleName().equals("ADMIN")
                        && role.getCreateUserId().equals(100L)
                        && role.getUpdateUserId().equals(100L)
                        && role.getSecretToken() != null
        ));

        verify(roleHistoryService)
                .recordHistory(10L, OperateTypeConstant.CREATE);
    }

    /**
     * 创建角色失败
     */
    @Test
    void create_insertFail_throwException() {
        // given
        RoleCreateDTO dto = new RoleCreateDTO();
        dto.setRoleName("ADMIN");

        when(roleMapper.insert(any(Role.class)))
                .thenReturn(0); // 插入失败

        // then
        assertThrows(RoleCreateFailedException.class,
                () -> roleService.create(dto));

        verify(roleHistoryService, never())
                .recordHistory(anyLong(), any());
    }

    // ========================= pageQuery() =========================

    /**
     * 分页查询成功
     */
    @Test
    void pageQuery_success() {
        // given
        RolePageQueryDTO dto = new RolePageQueryDTO();
        dto.setPageNum(1);
        dto.setPageSize(10);

        // 构造 Page<Role>
        Page<Role> page = new Page<>();
        page.setTotal(2);

        Role r1 = new Role();
        r1.setRoleId(1L);
        r1.setRoleName("ADMIN");

        Role r2 = new Role();
        r2.setRoleId(2L);
        r2.setRoleName("USER");

        page.add(r1);
        page.add(r2);

        when(roleMapper.pageQuery(dto)).thenReturn(page);

        // when
        PageResult result = roleService.pageQuery(dto);

        // then
        assertNotNull(result);
        assertEquals(2, result.getTotal());

        List<RoleVO> records = result.getRecords();
        assertEquals(2, records.size());
        assertEquals("ADMIN", records.get(0).getRoleName());
        assertEquals("USER", records.get(1).getRoleName());

        verify(roleMapper).pageQuery(dto);
    }

    /**
     * 分页参数兜底
     */
    @Test
    void pageQuery_pageParamNull_useDefault() {
        // given
        RolePageQueryDTO dto = new RolePageQueryDTO();
        // pageNum / pageSize 都不设置

        Page<Role> emptyPage = new Page<>();
        emptyPage.setTotal(0);

        when(roleMapper.pageQuery(dto)).thenReturn(emptyPage);

        // when
        PageResult result = roleService.pageQuery(dto);

        // then
        assertEquals(0, result.getTotal());
        assertTrue(result.getRecords().isEmpty());

        // 关键：normalizePage 确实生效
        assertNotNull(dto.getPageNum());
        assertNotNull(dto.getPageSize());
        assertTrue(dto.getPageNum() >= 1);
        assertTrue(dto.getPageSize() > 0);

        verify(roleMapper).pageQuery(dto);
    }

    // ========================= getByRoleId() =========================

    /**
     * 角色存在
     */
    @Test
    void getByRoleId_success() {
        // given
        Long roleId = 1L;

        Role role = new Role();
        role.setRoleId(roleId);
        role.setRoleName("ADMIN");
        role.setRoleDisplayName("管理员");

        when(roleMapper.getByRoleId(roleId))
                .thenReturn(role);

        // when
        RoleVO result = roleService.getByRoleId(roleId);

        // then
        assertNotNull(result);
        assertEquals(roleId, result.getRoleId());
        assertEquals("ADMIN", result.getRoleName());
        assertEquals("管理员", result.getRoleDisplayName());

        verify(roleMapper).getByRoleId(roleId);
    }

    /**
     * 角色不存在
     */
    @Test
    void getByRoleId_roleNotExist_throwException() {
        // given
        Long roleId = 99L;

        when(roleMapper.getByRoleId(roleId))
                .thenReturn(null);

        // then
        assertThrows(RoleNotExistException.class,
                () -> roleService.getByRoleId(roleId));

        verify(roleMapper).getByRoleId(roleId);
    }

    // ========================= update() =========================

    /**
     * 更新成功
     */
    @Test
    void update_success() {
        // given
        Long roleId = 1L;

        Role dbRole = new Role();
        dbRole.setRoleId(roleId);
        dbRole.setStatus(StatusConstant.ENABLED);

        RoleDTO dto = new RoleDTO();
        dto.setRoleName("ADMIN");
        dto.setRoleDisplayName("管理员");
        dto.setDescription("系统管理员");
        dto.setVersion(1);
        dto.setSecretToken("old-token");

        when(roleMapper.getByRoleId(roleId)).thenReturn(dbRole);
        when(roleMapper.update(any(Role.class))).thenReturn(1);

        // when
        roleService.update(roleId, dto);

        // then
        verify(roleMapper).update(argThat(role ->
                role.getRoleId().equals(roleId)
                        && role.getRoleName().equals("ADMIN")
                        && role.getVersion().equals(1)
                        && role.getSecretToken().equals("old-token")
                        && role.getUpdateUserId().equals(100L)
                        && role.getNewSecretToken() != null
        ));

        verify(roleHistoryService)
                .recordHistory(roleId, OperateTypeConstant.UPDATE);
    }

    /**
     * 角色不存在
     */
    @Test
    void update_roleNotExist_throwException() {
        // given
        Long roleId = 99L;

        when(roleMapper.getByRoleId(roleId))
                .thenReturn(null);

        RoleDTO roleDTO = new RoleDTO();

        // then
        assertThrows(RoleNotExistException.class,
                () -> roleService.update(roleId, roleDTO));

        verify(roleMapper, never()).update(any());
        verify(roleHistoryService, never()).recordHistory(anyLong(), any());
    }

    /**
     * 角色被禁用
     */
    @Test
    void update_roleDisabled_throwException() {
        // given
        Long roleId = 2L;

        Role dbRole = new Role();
        dbRole.setRoleId(roleId);
        dbRole.setStatus(StatusConstant.DISABLED);

        when(roleMapper.getByRoleId(roleId))
                .thenReturn(dbRole);

        RoleDTO roleDTO = new RoleDTO();

        // then
        assertThrows(RoleForbiddenException.class,
                () -> roleService.update(roleId, roleDTO));

        verify(roleMapper, never()).update(any());
        verify(roleHistoryService, never()).recordHistory(anyLong(), any());
    }

    /**
     * 更新失败（乐观锁冲突）
     */
    @Test
    void update_updateFail_throwException() {
        // given
        Long roleId = 3L;

        Role dbRole = new Role();
        dbRole.setRoleId(roleId);
        dbRole.setStatus(StatusConstant.ENABLED);

        RoleDTO dto = new RoleDTO();
        dto.setVersion(1);
        dto.setSecretToken("old-token");

        when(roleMapper.getByRoleId(roleId)).thenReturn(dbRole);
        when(roleMapper.update(any(Role.class))).thenReturn(0); // 更新失败

        // then
        assertThrows(UpdateFailedException.class,
                () -> roleService.update(roleId, dto));

        verify(roleHistoryService, never())
                .recordHistory(anyLong(), any());
    }

    // ========================= changeStatus() =========================

    /**
     * 状态修改成功
     */
    @Test
    void changeStatus_success() {
        // given
        Long roleId = 1L;
        Integer newStatus = StatusConstant.DISABLED;

        Role dbRole = new Role();
        dbRole.setRoleId(roleId);
        dbRole.setStatus(StatusConstant.ENABLED);
        dbRole.setVersion(1);
        dbRole.setSecretToken("old-token");

        when(roleMapper.getByRoleId(roleId))
                .thenReturn(dbRole);
        when(roleMapper.update(any(Role.class)))
                .thenReturn(1);

        // when
        roleService.changeStatus(roleId, newStatus);

        // then
        verify(roleMapper).update(argThat(role ->
                role.getRoleId().equals(roleId)
                        && role.getStatus().equals(newStatus)
                        && role.getVersion().equals(1)
                        && role.getSecretToken().equals("old-token")
                        && role.getNewSecretToken() != null
                        && role.getUpdateUserId().equals(100L)
        ));

        verify(roleHistoryService)
                .recordHistory(roleId, OperateTypeConstant.UPDATE);
    }

    /**
     * 角色不存在
     */
    @Test
    void changeStatus_roleNotExist_throwException() {
        // given
        Long roleId = 99L;

        when(roleMapper.getByRoleId(roleId))
                .thenReturn(null);

        // then
        assertThrows(RoleNotExistException.class,
                () -> roleService.changeStatus(roleId, StatusConstant.DISABLED));

        verify(roleMapper, never()).update(any());
        verify(roleHistoryService, never()).recordHistory(anyLong(), any());
    }

    /**
     * 状态未改变
     */
    @Test
    void changeStatus_statusNotChange_throwException() {
        // given
        Long roleId = 2L;

        Role dbRole = new Role();
        dbRole.setRoleId(roleId);
        dbRole.setStatus(StatusConstant.ENABLED);

        when(roleMapper.getByRoleId(roleId))
                .thenReturn(dbRole);

        // then
        assertThrows(StatusNotChangeException.class,
                () -> roleService.changeStatus(roleId, StatusConstant.ENABLED));

        verify(roleMapper, never()).update(any());
        verify(roleHistoryService, never()).recordHistory(anyLong(), any());
    }

    /**
     * update 失败（乐观锁冲突）
     */
    @Test
    void changeStatus_updateFail_throwException() {
        // given
        Long roleId = 3L;

        Role dbRole = new Role();
        dbRole.setRoleId(roleId);
        dbRole.setStatus(StatusConstant.ENABLED);
        dbRole.setVersion(1);
        dbRole.setSecretToken("old-token");

        when(roleMapper.getByRoleId(roleId))
                .thenReturn(dbRole);
        when(roleMapper.update(any(Role.class)))
                .thenReturn(0); // 更新失败

        // then
        assertThrows(UpdateFailedException.class,
                () -> roleService.changeStatus(roleId, StatusConstant.DISABLED));

        verify(roleHistoryService, never())
                .recordHistory(anyLong(), any());
    }












}
