package com.ymjrhk.rbac.service.impl;

import com.ymjrhk.rbac.context.UserContext;
import com.ymjrhk.rbac.entity.User;
import com.ymjrhk.rbac.entity.UserRole;
import com.ymjrhk.rbac.exception.AssignmentRoleFailedException;
import com.ymjrhk.rbac.exception.RoleNotExistException;
import com.ymjrhk.rbac.exception.UserNotExistException;
import com.ymjrhk.rbac.mapper.RoleMapper;
import com.ymjrhk.rbac.mapper.UserMapper;
import com.ymjrhk.rbac.mapper.UserRoleMapper;
import com.ymjrhk.rbac.service.UserRoleService;
import com.ymjrhk.rbac.vo.RoleVO;
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
     * 给用户分配角色
     *
     * @param userId
     * @param roleIds
     */
    @Override
    @Transactional
    public void assignRolesToUser(Long userId, List<Long> roleIds) { // TODO: 需要version吗
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

        // 3. 从 sys_user_role 表中删除原本的关联
        userRoleMapper.deleteByUserId(userId);

        // 4. 新插入数据
        Long operateUserId = UserContext.getCurrentUserId();

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

        // TODO: 5. 可选：写审计日志
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
}
