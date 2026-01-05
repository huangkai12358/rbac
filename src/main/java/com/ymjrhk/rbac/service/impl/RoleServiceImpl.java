package com.ymjrhk.rbac.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.ymjrhk.rbac.context.BaseContext;
import com.ymjrhk.rbac.dto.*;
import com.ymjrhk.rbac.entity.Role;
import com.ymjrhk.rbac.exception.*;
import com.ymjrhk.rbac.mapper.RoleMapper;
import com.ymjrhk.rbac.result.PageResult;
import com.ymjrhk.rbac.service.RoleService;
import com.ymjrhk.rbac.service.base.BaseService;
import com.ymjrhk.rbac.vo.RoleVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static com.ymjrhk.rbac.constant.MessageConstant.*;
import static com.ymjrhk.rbac.constant.StatusConstant.DISABLE;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl extends BaseService implements RoleService {
    private final RoleMapper roleMapper;

    /**
     * 创建角色
     * @param roleCreateDTO
     */
    @Override
    public void create(RoleCreateDTO roleCreateDTO) {
        Role role = BeanUtil.copyProperties(roleCreateDTO, Role.class);

        String secretToken = UUID.randomUUID().toString();

        // 查询当前用户id
        Long userId = BaseContext.getCurrentUserId();

        role.setSecretToken(secretToken);
        role.setCreateUserId(userId);
        role.setUpdateUserId(userId);

        int result = roleMapper.insert(role);
        if (result != 1) {
            throw new RoleCreateFailed(ROLE_CREATE_FAILED); // 创建角色失败
        }
    }

    /**
     * 角色分页查询
     * @param rolePageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(RolePageQueryDTO rolePageQueryDTO) {
        PageHelper.startPage(rolePageQueryDTO.getPageNum(), rolePageQueryDTO.getPageSize());

        Page<Role> page = roleMapper.pageQuery(rolePageQueryDTO);

        long total = page.getTotal();

        List<RoleVO> records = page.getResult().stream()
                                   .map(role -> BeanUtil.copyProperties(role, RoleVO.class))
                                   .toList();
        return new PageResult(total, records);
    }

    /**
     * 根据 roleId 查询角色
     * @param roleId
     * @return
     */
    @Override
    public RoleVO getByRoleId(Long roleId) {
        Role role = roleMapper.getByRoleId(roleId);

        // 角色不存在
        if (role == null) {
            throw new RoleNotExistException(ROLE_NOT_EXIST);
        }

        return BeanUtil.copyProperties(role, RoleVO.class);
    }

    /**
     * 修改角色
     * @param roleDTO
     */
    @Override
    @Transactional
    public void update(RoleDTO roleDTO) {
        Role dbRole = roleMapper.getByRoleId(roleDTO.getRoleId());

        // 角色不存在
        if (dbRole == null) {
            throw new RoleNotExistException(ROLE_NOT_EXIST);
        }

        // 角色被禁用，不能修改
        if (Objects.equals(dbRole.getStatus(), DISABLE)) {
            throw new RoleForbiddenException(ROLE_FORBIDDEN);
        }

        Role role = new Role();
        role.setRoleId(roleDTO.getRoleId());
        role.setRoleName(roleDTO.getRoleName());
        role.setRoleDisplayName(roleDTO.getRoleDisplayName());
        role.setDescription(roleDTO.getDescription());

        Integer version = dbRole.getVersion(); // 获取版本号
        String secretToken = dbRole.getSecretToken(); // 获取旧 secretToken
        String newSecretToken = UUID.randomUUID().toString();
        Long updateUserId = BaseContext.getCurrentUserId();

        fillOptimisticLockFields(role, version, secretToken, newSecretToken, updateUserId);

        doUpdate(role);
        // TODO:写历史表和审计表
    }

    /**
     * 启用或禁用角色
     * @param roleId
     * @param status
     */
    @Override
    @Transactional
    public void changeStatus(Long roleId, Integer status) {
        // 1. 查数据库
        Role dbRole = roleMapper.getByRoleId(roleId);
        if (dbRole == null) { // 角色不存在
            throw new RoleNotExistException(ROLE_NOT_EXIST);
        }
        // 2. 构造“更新用实体”（只放必要字段）
        Role role = new Role();
        role.setRoleId(roleId);

        // 3. 调 BaseService 的模板方法
        changeStatus(dbRole, role, status);

        // 4. 执行 update
        doUpdate(role);
        // TODO:写历史表和审计表
    }

    /**
     * 公共的调用 mapper 的 update() 方法
     * @param role
     */
    public void doUpdate(Role role) {
        int result = roleMapper.update(role);
        if (result != 1) {
            throw new UpdateFailedException(UPDATE_FAILED); // 数据已被修改，请刷新重试
        }
    }
}
