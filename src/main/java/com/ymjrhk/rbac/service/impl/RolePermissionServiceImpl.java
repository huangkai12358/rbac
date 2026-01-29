package com.ymjrhk.rbac.service.impl;

import com.ymjrhk.rbac.context.UserContext;
import com.ymjrhk.rbac.entity.Role;
import com.ymjrhk.rbac.entity.RolePermission;
import com.ymjrhk.rbac.exception.*;
import com.ymjrhk.rbac.mapper.*;
import com.ymjrhk.rbac.service.RolePermissionService;
import com.ymjrhk.rbac.vo.PermissionVO;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.ymjrhk.rbac.constant.CacheConstant.*;
import static com.ymjrhk.rbac.constant.MessageConstant.*;
import static com.ymjrhk.rbac.constant.RoleNameConstant.SUPER_ADMIN;
import static com.ymjrhk.rbac.constant.StatusConstant.DISABLED;
import static com.ymjrhk.rbac.constant.StatusConstant.ENABLED;

@Service
@RequiredArgsConstructor
public class RolePermissionServiceImpl implements RolePermissionService {
    private final RoleMapper roleMapper;

    private final RolePermissionMapper rolePermissionMapper;

    private final PermissionMapper permissionMapper;

    private final UserMapper userMapper;

    private final UserRoleMapper userRoleMapper;

    /**
     * 给角色分配权限
     *
     * @param roleId
     * @param permissionIds
     */
    @Override
    @Transactional
    @Caching(evict = {
            // 角色权限：只清当前 role
            @CacheEvict(
                    cacheNames = ROLE_PERMISSIONS,
                    key = "#roleId"
            ),
            // 用户权限 & 鉴权：全清（不知道哪些用户绑定了这个 role）
            @CacheEvict(
                    cacheNames = {
                            USER_PERMISSIONS,
                            USER_AUTH
                    },
                    allEntries = true
            )
    })
    public void assignPermissionsToRole(Long roleId, List<Long> permissionIds) { // TODO: 需要version吗
//        A = 我（用户）拥有的权限（非禁用）
//        B = 角色拥有的权限（非禁用）
//        C = 我这次提交勾选的权限（前端传 permissionIds）

        /*
        1. 校验角色是否存在或者被禁用
           - 如果是超级管理员角色 → 直接拒绝
        2. 提交勾选的权限是否为空
             1. 否 → 到步骤 3
             2. 是 → 执行步骤 4、6、7
        3. 校验提交勾选的权限是否存在或者被禁用
        4. 构造授权边界 A（我能管理的权限）
             1. 普通管理员：我拥有的权限（非禁用）
             2. 超级管理员：系统中所有非禁用权限
        5. 提交勾选的权限是否有超出我的权限（是否 ⊆ A）
        6. 查角色拥有的权限 B（非禁用）
        7. 删除角色权限中角色和我都拥有的权限 B ∩ A
        8. 插入新分配的权限 C
        */

        /* ========= 1. 校验角色是否存在 / 是否被禁用 ========= */
        Role dbRole = roleMapper.getByRoleId(roleId);
        if (dbRole == null) {
            throw new RoleNotExistException(ROLE_NOT_EXIST);
        }
        if (dbRole.getStatus() == DISABLED) {
            throw new RoleForbiddenException(ROLE_FORBIDDEN);
        }

        // 禁止给超级管理员角色分配权限
        if (dbRole.getRoleName().equals(SUPER_ADMIN)) {
            throw new AssignmentPermissionToSuperAdminException(ASSIGNMENT_PERMISSION_TO_SUPER_ADMIN);
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

        /* ========= 4. 构造授权边界 A（我能管理的权限） ========= */
        Long operatorId = UserContext.getCurrentUserId();

        Set<Long> myPermissionSet;

        boolean isSuperAdminUser =
                userRoleMapper.userHasRole(operatorId, SUPER_ADMIN);

        if (isSuperAdminUser) {
            // 超级管理员：A = 所有非禁用权限
            List<Long> allEnabledPermissionIds = permissionMapper.selectAllEnabledPermissionIds();
            myPermissionSet = new HashSet<>(allEnabledPermissionIds);
        } else {
            // 普通管理员：A = 我拥有的权限（非禁用）
            List<Long> myPermissionIds = userMapper.selectPermissionIdsByUserIdAndStatus(operatorId, ENABLED); // 重点：此处查的是用户（我）的权限
            myPermissionSet = new HashSet<>(myPermissionIds);
        }

        /* ========= 5. 校验勾选的权限是否有超出我的权限 ========= */
        if (!assignEmpty) {
            boolean illegal = permissionIds.stream().anyMatch(id -> !myPermissionSet.contains(id));
            if (illegal) {
                throw new AssignmentNotOwnedPermissionException(ASSIGNMENT_NOT_OWNED_PERMISSION);
            }
        }

        /* ========= 6. 查角色拥有的权限 B（非禁用） ========= */
        List<Long> rolePermissionIds = rolePermissionMapper
                .selectPermissionIdsByRoleIdAndStatus(roleId, ENABLED); // 重点：此处查的是（被分配）角色的权限

        Set<Long> rolePermissionSet = new HashSet<>(rolePermissionIds);

        /* ========= 7. 删除：角色和我都拥有的权限 B ∩ A ========= */
        Set<Long> deletablePermissions = new HashSet<>(rolePermissionSet);
        deletablePermissions.retainAll(myPermissionSet); // B ∩ A

        if (!deletablePermissions.isEmpty()) {
            rolePermissionMapper.deleteByRoleIdAndPermissionIds(roleId, deletablePermissions);
        }

        /* ========= 8. 插入：我新分配的权限 C ========= */
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
    @Cacheable(
            cacheNames = ROLE_PERMISSIONS,
            key = "#roleId"
    )
    public List<PermissionVO> getRolePermissions(Long roleId) {
        // 1. 查 roleId 是否存在
        Role role = roleMapper.getByRoleId(roleId);
        if (role == null) {
            throw new RoleNotExistException(ROLE_NOT_EXIST);
        }

        // 2. 是否是超级管理员
        if (role.getRoleName().equals(SUPER_ADMIN)) {
            return permissionMapper.listAllActivePermissions();
        }

        // 3. 查 roleId 对应的权限
        return rolePermissionMapper.selectPermissionsByRoleId(roleId);
    }
}
