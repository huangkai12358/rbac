package com.ymjrhk.rbac.service.impl;

import com.ymjrhk.rbac.constant.StatusConstant;
import com.ymjrhk.rbac.context.UserContext;
import com.ymjrhk.rbac.entity.Role;
import com.ymjrhk.rbac.entity.User;
import com.ymjrhk.rbac.entity.UserRole;
import com.ymjrhk.rbac.exception.*;
import com.ymjrhk.rbac.mapper.MeMapper;
import com.ymjrhk.rbac.mapper.RoleMapper;
import com.ymjrhk.rbac.mapper.UserMapper;
import com.ymjrhk.rbac.mapper.UserRoleMapper;
import com.ymjrhk.rbac.service.UserRoleService;
import com.ymjrhk.rbac.vo.AssignableRoleVO;
import com.ymjrhk.rbac.vo.RoleVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static com.ymjrhk.rbac.constant.MessageConstant.*;
import static com.ymjrhk.rbac.constant.StatusConstant.DISABLED;
import static com.ymjrhk.rbac.constant.StatusConstant.ENABLED;

@Service
@RequiredArgsConstructor
public class UserRoleServiceImpl implements UserRoleService {
    private final UserMapper userMapper;

    private final UserRoleMapper userRoleMapper;

    private final RoleMapper roleMapper;
    private final MeMapper meMapper;

    /**
     * 给用户分配角色
     *
     * @param userId
     * @param roleIds
     */
    @Override
    @Transactional
    public void assignRolesToUser(Long userId, List<Long> roleIds) { // TODO: 需要version吗
//        A = 我拥有的角色（非禁用）
//        B = 用户拥有的角色（非禁用）
//        C = 我这次提交勾选的角色（前端传 roleIds）
//        最终用户角色 = (B - A) ∪ C

        /*
        1. 校验用户是否存在或者被禁用
        2. 提交勾选的角色是否为空
             1. 否，到步骤 3
             2. 是，执行步骤 4、6、7
        3. 校验提交勾选的角色是否存在或者被禁用
        4. 查我拥有的角色（非禁用）
        5. 提交勾选的角色是否有超出我的角色？
        6. 查用户拥有的角色（非禁用）
        7. 删除用户角色中用户和我都拥有的角色
        8. 插入我从我的角色中给用户新分配的角色（用户原来有、我没有的角色保持不变）
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

        /* ========= 4. 查我拥有的角色（非禁用） ========= */
        Long operatorId = UserContext.getCurrentUserId();
        List<Long> myRoleIds = userRoleMapper
                .selectRoleIdsByUserIdAndStatus(operatorId, ENABLED);

        Set<Long> myRoleSet = new HashSet<>(myRoleIds);

        /* ========= 5. 校验勾选的角色是否有超出我的角色 ========= */
        if (!assignEmpty) {
            boolean illegal = roleIds.stream().anyMatch(id -> !myRoleSet.contains(id));
            if (illegal) {
                throw new AssignmentRoleDeniedException(ASSIGNMENT_ROLE_DENIED);
            }
        }

        /* ========= 6. 查用户拥有的角色（非禁用） ========= */
        List<Long> userRoleIds = userRoleMapper
                .selectRoleIdsByUserIdAndStatus(userId, ENABLED);

        Set<Long> userRoleSet = new HashSet<>(userRoleIds);

        /* ========= 7. 删除：用户和我都拥有的角色 ========= */
        Set<Long> deletableRoles = new HashSet<>(userRoleSet);
        deletableRoles.retainAll(myRoleSet); // B ∩ A

        if (!deletableRoles.isEmpty()) {
            userRoleMapper.deleteByUserIdAndRoleIds(userId, deletableRoles);
        }

        /* ========= 8. 插入：我新分配的角色 ========= */
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

    /**
     * 查看用户的角色和我的角色的关系
     * @param userId
     * @return
     */
    @Override
    public List<AssignableRoleVO> getAssignableRoles(Long userId) {

        /* ========= 1. 校验用户是否存在 / 是否禁用 ========= */
        User user = userMapper.getByUserId(userId);
        if (user == null) {
            throw new UserNotExistException(USER_NOT_EXIST);
        }
        if (user.getStatus() == StatusConstant.DISABLED) {
            throw new UserForbiddenException(USER_FORBIDDEN);
        }

        /* ========= 2. 查我拥有的角色（非禁用） ========= */
        Long operatorId = UserContext.getCurrentUserId();

        List<Role> myRoles =
                roleMapper.selectRolesByUserIdAndStatus(
                        operatorId, ENABLED
                );

        Map<Long, Role> myRoleMap = myRoles.stream()
                                           .collect(Collectors.toMap(Role::getRoleId, r -> r));

        /* ========= 3. 查用户拥有的角色（非禁用） ========= */
        List<Role> userRoles =
                roleMapper.selectRolesByUserIdAndStatus(
                        userId, StatusConstant.ENABLED
                );

        Map<Long, Role> userRoleMap = userRoles.stream()
                                               .collect(Collectors.toMap(Role::getRoleId, r -> r));

        /* ========= 4. 合并角色集合（A ∪ B） ========= */
        Set<Long> allRoleIds = new HashSet<>();
        allRoleIds.addAll(myRoleMap.keySet());
        allRoleIds.addAll(userRoleMap.keySet());

        /* ========= 5. 组装返回 VO ========= */
        List<AssignableRoleVO> result = new ArrayList<>();

        for (Long roleId : allRoleIds) {
            Role role = myRoleMap.getOrDefault(
                    roleId,
                    userRoleMap.get(roleId)
            );

            AssignableRoleVO vo = new AssignableRoleVO();
            vo.setRoleId(roleId);
            vo.setRoleDisplayName(role.getRoleDisplayName());
            vo.setOwnedByMe(myRoleMap.containsKey(roleId));
            vo.setOwnedByUser(userRoleMap.containsKey(roleId));

            result.add(vo);
        }

        // 可选：按角色名排序，前端体验更好 // TODO: 按 sort 排序
        result.sort(Comparator.comparing(AssignableRoleVO::getRoleDisplayName));

        return result;
    }

}
