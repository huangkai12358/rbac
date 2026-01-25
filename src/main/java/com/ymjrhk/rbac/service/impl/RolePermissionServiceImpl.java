package com.ymjrhk.rbac.service.impl;

import com.ymjrhk.rbac.constant.RoleNameConstant;
import com.ymjrhk.rbac.context.UserContext;
import com.ymjrhk.rbac.entity.Role;
import com.ymjrhk.rbac.entity.RolePermission;
import com.ymjrhk.rbac.entity.User;
import com.ymjrhk.rbac.entity.UserRole;
import com.ymjrhk.rbac.exception.*;
import com.ymjrhk.rbac.mapper.PermissionMapper;
import com.ymjrhk.rbac.mapper.RoleMapper;
import com.ymjrhk.rbac.mapper.RolePermissionMapper;
import com.ymjrhk.rbac.mapper.UserMapper;
import com.ymjrhk.rbac.service.RolePermissionService;
import com.ymjrhk.rbac.vo.PermissionVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.ymjrhk.rbac.constant.MessageConstant.*;
import static com.ymjrhk.rbac.constant.StatusConstant.DISABLED;
import static com.ymjrhk.rbac.constant.StatusConstant.ENABLED;

@Service
@RequiredArgsConstructor
public class RolePermissionServiceImpl implements RolePermissionService {
    private final RoleMapper roleMapper;

    private final RolePermissionMapper rolePermissionMapper;

    private final PermissionMapper permissionMapper;
    private final UserMapper userMapper;

    /**
     * 给角色分配权限
     *
     * @param roleId
     * @param permissionIds
     */
    @Override
    @Transactional
    public void assignPermissionsToRole(Long roleId, List<Long> permissionIds) { // TODO: 需要version吗
//        A = 我拥有的权限（非禁用）
//        B = 角色拥有的权限（非禁用）
//        C = 我这次提交勾选的权限（前端传 permissionIds）

        /* ========= 1. 校验角色是否存在 / 是否被禁用 ========= */
        Role dbRole = roleMapper.getByRoleId(roleId);
        if (dbRole == null) {
            throw new RoleNotExistException(ROLE_NOT_EXIST);
        }
        if (dbRole.getStatus() == DISABLED) {
            throw new RoleForbiddenException(ROLE_FORBIDDEN);
        }

        /* ========= 2. permissionIds 是否为空 ========= */
        // 如果为空，执行步骤 4、6、7
        boolean assignEmpty = (permissionIds == null || permissionIds.isEmpty());

        /* ========= 3. 校验提交权限是否存在 / 是否被禁用 ========= */
        if (!assignEmpty) {
            List<Long> validPermissionIds = permissionMapper.selectEnabledPermissionIds(permissionIds);
            if (validPermissionIds.size() != permissionIds.size()) {
                throw new PermissionNotExistOrDisabledException(PERMISSION_NOT_EXIST_OR_DISABLED); // 分配的权限不存在或者被禁用
            }
        }

        /* ========= 4. 查我拥有的权限（非禁用） ========= */
        Long operatorId = UserContext.getCurrentUserId();
        List<Long> myPermissionIds = userMapper
                .selectPermissionIdsByUserIdAndStatus(operatorId, ENABLED); // 重点：此处查的是用户（我）的权限

        Set<Long> myPermissionSet = new HashSet<>(myPermissionIds);

        /* ========= 5. 校验勾选的权限是否有超出我的权限 ========= */
        if (!assignEmpty) {
            boolean illegal = permissionIds.stream().anyMatch(id -> !myPermissionSet.contains(id));
            if (illegal) {
                throw new AssignmentPermissionDeniedException(ASSIGNMENT_PERMISSION_DENIED);
            }
        }

        /* ========= 6. 查角色拥有的权限（非禁用） ========= */
        List<Long> rolePermissionIds = rolePermissionMapper
                .selectPermissionIdsByRoleIdAndStatus(roleId, ENABLED); // 重点：此处查的是（被分配）角色的权限

        Set<Long> rolePermissionSet = new HashSet<>(rolePermissionIds);

        /* ========= 7. 删除：角色和我都拥有的权限 ========= */
        Set<Long> deletablePermissions = new HashSet<>(rolePermissionSet);
        deletablePermissions.retainAll(myPermissionSet); // B ∩ A

        if (!deletablePermissions.isEmpty()) {
            rolePermissionMapper.deleteByRoleIdAndPermissionIds(roleId, deletablePermissions);
        }

        /* ========= 8. 插入：我新分配的权限 ========= */
        if (assignEmpty) {
            return; // 我不再给任何权限，结束
        }

        List<RolePermission> relations = permissionIds.stream()
                                                      .map(permissionId -> {
                                                          RolePermission rp = new RolePermission();
                                                          rp.setRoleId(roleId);
                                                          rp.setPermissionId(permissionId);
                                                          rp.setCreateUserId(operatorId);
                                                          return rp;
                                                      })
                                                      .toList();

        int inserted = rolePermissionMapper.batchInsert(relations);
        if (inserted != relations.size()) {
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
