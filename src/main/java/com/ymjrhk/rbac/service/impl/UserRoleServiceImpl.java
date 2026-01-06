package com.ymjrhk.rbac.service.impl;

import com.ymjrhk.rbac.context.BaseContext;
import com.ymjrhk.rbac.entity.User;
import com.ymjrhk.rbac.entity.UserRole;
import com.ymjrhk.rbac.exception.*;
import com.ymjrhk.rbac.mapper.RoleMapper;
import com.ymjrhk.rbac.mapper.UserMapper;
import com.ymjrhk.rbac.mapper.UserRoleMapper;
import com.ymjrhk.rbac.service.UserRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.ymjrhk.rbac.constant.MessageConstant.*;

@Service
@RequiredArgsConstructor
public class UserRoleServiceImpl implements UserRoleService {
    private final UserMapper userMapper;

    private final UserRoleMapper userRoleMapper;

    private final RoleMapper roleMapper;

    /**
     * 用户分配角色
     * @param userId
     * @param roleIds
     */
    @Override
    @Transactional
    public void userAssignRoles(Long userId, List<Long> roleIds) { // TODO: 需要version吗
        // 1. 校验用户是否存在
        User dbUser = userMapper.getByUserId(userId);
        if (dbUser == null) { // 用户不存在
            throw new UserNotExistException(USER_NOT_EXIST);
        }

        // 2. 校验角色是否存在（!!不要n次roleMapper.getByRoleId(roleId)，下面方法一条SQL，O(1)复杂度）
        if (roleIds != null && !roleIds.isEmpty()) {
            List<Long> existRoleIds = roleMapper.selectExistingRoleIds(roleIds);
            if (existRoleIds.size() != roleIds.size()) { // 有某个角色不存在
                throw new RoleNotExistException(ROLE_NOT_EXIST);
            }
        }

        // 2. 从 sys_user_role 表中删除原本的关联
        userRoleMapper.deleteByUserId(userId);

        // 3. 新插入数据
        Long operateUserId = BaseContext.getCurrentUserId();

        if (roleIds != null && !roleIds.isEmpty()) {
            List<UserRole> relations = roleIds.stream()
                                              .map(roleId -> {
                                                  UserRole userRole = new UserRole();
                                                  userRole.setUserId(userId);
                                                  userRole.setRoleId(roleId);
                                                  userRole.setCreateUserId(operateUserId);
                                                  return userRole;
                                              })
                                              .toList();
            int result = userRoleMapper.batchInsert(relations);
            if (result == 0) { // 分配角色失败
                throw new AssignmentRoleFailedException(ASSIGNMENT_ROLE_FAILED);
            }
        }

        // TODO: 4. 可选：写审计日志
    }
}
