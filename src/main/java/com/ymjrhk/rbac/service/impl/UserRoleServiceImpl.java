package com.ymjrhk.rbac.service.impl;

import com.ymjrhk.rbac.context.UserContext;
import com.ymjrhk.rbac.entity.User;
import com.ymjrhk.rbac.entity.UserRole;
import com.ymjrhk.rbac.exception.*;
import com.ymjrhk.rbac.mapper.RoleMapper;
import com.ymjrhk.rbac.mapper.UserMapper;
import com.ymjrhk.rbac.mapper.UserRoleMapper;
import com.ymjrhk.rbac.service.UserRoleService;
import com.ymjrhk.rbac.vo.RoleVO;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static com.ymjrhk.rbac.constant.CacheConstant.*;
import static com.ymjrhk.rbac.constant.MessageConstant.*;
import static com.ymjrhk.rbac.constant.RoleNameConstant.SUPER_ADMIN;
import static com.ymjrhk.rbac.constant.StatusConstant.DISABLED;
import static com.ymjrhk.rbac.constant.StatusConstant.ENABLED;

@Service
@RequiredArgsConstructor
public class UserRoleServiceImpl implements UserRoleService {
    private final UserMapper userMapper;

    private final UserRoleMapper userRoleMapper;

    private final RoleMapper roleMapper;

    /**
     * 给用户分配角色
     *
     * @param userId
     * @param roleIds
     */
    @Override
    @Transactional
    @CacheEvict(
            cacheNames = {
                    USER_ROLES,
                    USER_PERMISSIONS,
                    USER_AUTH
            },
            key = "#userId"
    )
    public void assignRolesToUser(Long userId, List<Long> roleIds) { // TODO: 需要version吗
//        A = 我拥有的角色（非禁用）
//        B = 用户拥有的角色（非禁用）
//        C = 我这次提交勾选的角色（前端传 roleIds）
//        最终用户角色 = (B - A) ∪ C

        /*
        1. 校验用户是否存在或者被禁用
        2. 提交勾选的角色是否为空
             - 否 → 到步骤 3
             - 是 → 执行步骤 4、6、7
        3. 校验提交勾选的角色是否存在或者被禁用
        4. 构造授权边界 A（我能管理的角色）
             - 普通管理员：我拥有的角色（非禁用）
             - 超级管理员：所有非禁用角色
        5. 提交勾选的角色是否有超出我的角色？（ 是否 ⊆ A）
        6. 查用户拥有的角色（非禁用）
        7. 删除用户角色中用户和我都拥有的角色 B ∩ A
        8. 插入新分配的角色 C
        */

        /* ========= 1. 校验用户是否存在 / 是否被禁用 ========= */
        User dbUser = userMapper.getByUserId(userId);
        if (dbUser == null) {
            throw new UserNotExistException(USER_NOT_EXIST);
        }
        if (dbUser.getStatus() == DISABLED) {
            throw new UserForbiddenException(USER_FORBIDDEN);
        }

        /* ========= 2. roleIds 是否为空 ========= */
        // 如果为空，执行步骤 4、6、7
        boolean assignEmpty = (roleIds == null || roleIds.isEmpty());

        /* ========= 3. 校验提交角色是否存在 / 是否被禁用 ========= */
        if (!assignEmpty) {
            List<Long> validRoleIds = roleMapper.selectEnabledRoleIds(roleIds);
            if (validRoleIds.size() != roleIds.size()) {
                throw new RoleNotExistOrDisabledException(ROLE_NOT_EXIST_OR_DISABLED); // 分配的角色不存在或者被禁用
            }
        }

        /* ========= 4. 构造授权边界 A（我能管理的角色） ========= */
        Long operatorId = UserContext.getCurrentUserId();

        Set<Long> myRoleSet;

        // 判断我是否超级管理员
        boolean isSuperAdmin = userRoleMapper.userHasRole(operatorId, SUPER_ADMIN);

        if (isSuperAdmin) {
            // 超级管理员：A = 所有非禁用角色
            List<Long> allEnabledRoleIds = roleMapper.selectAllEnabledRoleIds();
            myRoleSet = new HashSet<>(allEnabledRoleIds);
        } else {
            // 普通管理员：A = 我拥有的角色（非禁用）
            List<Long> myRoleIds = userRoleMapper
                    .selectRoleIdsByUserIdAndStatus(operatorId, ENABLED);
            myRoleSet = new HashSet<>(myRoleIds);
        }

        /* ========= 5. 校验勾选的角色是否有超出我的角色 ========= */
        if (!assignEmpty) {
            boolean illegal = roleIds.stream().anyMatch(id -> !myRoleSet.contains(id));
            if (illegal) {
                throw new AssignmentNotOwnedRoleException(ASSIGNMENT_NOT_OWNED_ROLE);
            }
        }

        /* ========= 6. 查用户拥有的角色 B（非禁用） ========= */
        List<Long> userRoleIds = userRoleMapper
                .selectRoleIdsByUserIdAndStatus(userId, ENABLED);

        Set<Long> userRoleSet = new HashSet<>(userRoleIds);

        /* ========= 7. 删除：用户和我都拥有的角色 B ∩ A ========= */
        Set<Long> deletableRoles = new HashSet<>(userRoleSet);
        deletableRoles.retainAll(myRoleSet); // B ∩ A

        if (!deletableRoles.isEmpty()) {
            userRoleMapper.deleteByUserIdAndRoleIds(userId, deletableRoles);
        }

        /* ========= 8. 插入：我新分配的角色 C ========= */
        if (assignEmpty) {
            return; // 我不再给任何角色，结束
        }

        List<UserRole> relations = roleIds.stream()
                                          .map(roleId -> {
                                              UserRole ur = new UserRole();
                                              ur.setUserId(userId);
                                              ur.setRoleId(roleId);
                                              ur.setCreateUserId(operatorId);
                                              return ur;
                                          })
                                          .toList();

        int inserted = userRoleMapper.batchInsert(relations);
        if (inserted != relations.size()) {
            throw new AssignmentRoleFailedException(ASSIGNMENT_ROLE_FAILED);
        }
    }

    /**
     * 查询用户角色
     *
     * @param userId
     * @return
     */
    @Override
    @Cacheable(
            cacheNames = USER_ROLES,
            key = "#userId"
    )
    public List<RoleVO> getUserRoles(Long userId) {
        // 1. 查 userId 是否存在
        User user = userMapper.getByUserId(userId);
        if (user == null) {
            throw new UserNotExistException(USER_NOT_EXIST);
        }

        // 2. 查 userId 对应的角色
        return userRoleMapper.selectRolesByUserId(userId);
    }

    /**
     * 查用户是否拥有某角色
     *
     * @param userId
     * @param roleName
     * @return
     */
    @Override
    public boolean userHasRole(Long userId, String roleName) {
        // 暂时不用查 userId 和 roleName 是否存在，调用它的函数后面部分查了，且不存在也没关系
        return userRoleMapper.userHasRole(userId, roleName);
    }
}
