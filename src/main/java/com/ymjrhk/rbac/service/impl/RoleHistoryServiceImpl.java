package com.ymjrhk.rbac.service.impl;

import com.ymjrhk.rbac.constant.MessageConstant;
import com.ymjrhk.rbac.entity.Role;
import com.ymjrhk.rbac.entity.RoleHistory;
import com.ymjrhk.rbac.exception.HistoryInsertFailedException;
import com.ymjrhk.rbac.mapper.RoleHistoryMapper;
import com.ymjrhk.rbac.mapper.RoleMapper;
import com.ymjrhk.rbac.service.RoleHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 只保存成功的记录
 */
@Service
@RequiredArgsConstructor
public class RoleHistoryServiceImpl implements RoleHistoryService {

    private final RoleHistoryMapper roleHistoryMapper;

    private final RoleMapper roleMapper;

    @Override
    public void record(Long roleId, Integer operateType) {
        Role role = roleMapper.getByRoleId(roleId);

        RoleHistory roleHistory = new RoleHistory();

        roleHistory.setRoleId(roleId);
        roleHistory.setVersion(role.getVersion());
        roleHistory.setRoleName(role.getRoleName());
        roleHistory.setRoleDisplayName(role.getRoleDisplayName());
        roleHistory.setDescription(role.getDescription());
        roleHistory.setStatus(role.getStatus());
        roleHistory.setSecretToken(role.getSecretToken());
        roleHistory.setOperateType(operateType);
        roleHistory.setOperateTime(role.getUpdateTime());
        roleHistory.setOperatorId(role.getUpdateUserId());

        int result = roleHistoryMapper.insert(roleHistory);

        if (result != 1) { // 写入历史失败（应该极少）
            throw new HistoryInsertFailedException(MessageConstant.HistoryInsertFailed);
        }
    }
}
