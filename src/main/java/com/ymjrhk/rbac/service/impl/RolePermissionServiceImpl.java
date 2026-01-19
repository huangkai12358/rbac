package com.ymjrhk.rbac.service.impl;

import com.ymjrhk.rbac.constant.RoleNameConstant;
import com.ymjrhk.rbac.context.UserContext;
import com.ymjrhk.rbac.entity.Role;
import com.ymjrhk.rbac.entity.RolePermission;
import com.ymjrhk.rbac.exception.AssignmentPermissionFailedException;
import com.ymjrhk.rbac.exception.PermissionNotExistException;
import com.ymjrhk.rbac.exception.RoleNotExistException;
import com.ymjrhk.rbac.mapper.PermissionMapper;
import com.ymjrhk.rbac.mapper.RoleMapper;
import com.ymjrhk.rbac.mapper.RolePermissionMapper;
import com.ymjrhk.rbac.service.RolePermissionService;
import com.ymjrhk.rbac.vo.PermissionVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.ymjrhk.rbac.constant.MessageConstant.*;

@Service
@RequiredArgsConstructor
public class RolePermissionServiceImpl implements RolePermissionService {
    private final RoleMapper roleMapper;

    private final RolePermissionMapper rolePermissionMapper;

    private final PermissionMapper permissionMapper;

    /**
     * 给角色分配权限
     *
     * @param roleId
     * @param permissionIds
     */
    @Override
    @Transactional
    public void assignPermissionsToRole(Long roleId, List<Long> permissionIds) { // TODO: 需要version吗
        // 1. 校验角色是否存在
        Role dbRole = roleMapper.getByRoleId(roleId);
        if (dbRole == null) { // 角色不存在
            throw new RoleNotExistException(ROLE_NOT_EXIST);
        }

        // 2. 校验权限是否存在
        if (permissionIds != null && !permissionIds.isEmpty()) {
            List<Long> existPermissionIds = permissionMapper.selectExistingPermissionIds(permissionIds);
            if (existPermissionIds.size() != permissionIds.size()) { // 有某个权限不存在
                throw new PermissionNotExistException(PERMISSION_NOT_EXIST);
            }
        }

        // TODO：角色和权限不能被禁用

        // 3. 从 sys_role_permission 表中删除原本的关联
        rolePermissionMapper.deleteByRoleId(roleId);

        // 4. 新插入数据
        Long operateRoleId = UserContext.getCurrentUserId();

        List<RolePermission> relations = permissionIds.stream()
                                                      .map(permissionId -> {
                                                          RolePermission rolePermission = new RolePermission();
                                                          rolePermission.setRoleId(roleId);
                                                          rolePermission.setPermissionId(permissionId);
                                                          rolePermission.setCreateUserId(operateRoleId);
                                                          return rolePermission;
                                                      })
                                                      .toList();
        int result = rolePermissionMapper.batchInsert(relations);
        if (result == 0) { // 分配权限失败
            throw new AssignmentPermissionFailedException(ASSIGNMENT_PERMISSION_FAILED);
        }
    }

    /**
     * 查询角色权限
     *
     * @param roleId
     * @return
     */
    @Override
    public List<PermissionVO> getRolePermissions(Long roleId) {
        // 1. 查 roleId 是否存在
        Role role = roleMapper.getByRoleId(roleId);
        if (role == null) {
            throw new RoleNotExistException(ROLE_NOT_EXIST);
        }

        // 2. 是否是超级管理员
        if (role.getRoleName().equals(RoleNameConstant.SUPER_ADMIN)) {
            return permissionMapper.listAllActivePermissions();
        }

        // 3. 查 roleId 对应的权限
        return rolePermissionMapper.selectPermissionsByRoleId(roleId);
    }
}
