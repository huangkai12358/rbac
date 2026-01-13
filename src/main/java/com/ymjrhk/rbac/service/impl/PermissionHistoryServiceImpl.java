package com.ymjrhk.rbac.service.impl;

import com.ymjrhk.rbac.constant.MessageConstant;
import com.ymjrhk.rbac.entity.Permission;
import com.ymjrhk.rbac.entity.PermissionHistory;
import com.ymjrhk.rbac.exception.HistoryInsertFailedException;
import com.ymjrhk.rbac.mapper.PermissionHistoryMapper;
import com.ymjrhk.rbac.mapper.PermissionMapper;
import com.ymjrhk.rbac.service.PermissionHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 只保存成功的记录
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PermissionHistoryServiceImpl implements PermissionHistoryService {

    private final PermissionHistoryMapper permissionHistoryMapper;

    private final PermissionMapper permissionMapper;

    /**
     * 记录到历史表
     *
     * @param permissionId
     * @param operateType
     */
    @Override
    public void record(Long permissionId, Integer operateType) {
        log.debug("先读出新表所有数据，再拷贝到历史表中：");
        Permission permission = permissionMapper.getByPermissionId(permissionId);

        PermissionHistory permissionHistory = new PermissionHistory();

        permissionHistory.setPermissionId(permissionId);
        permissionHistory.setVersion(permission.getVersion());
        permissionHistory.setPermissionName(permission.getPermissionName());
        permissionHistory.setPermissionDisplayName(permission.getPermissionDisplayName());
        permissionHistory.setDescription(permission.getDescription());
        permissionHistory.setStatus(permission.getStatus());
        permissionHistory.setSecretToken(permission.getSecretToken());
        permissionHistory.setType(permission.getType());
        permissionHistory.setParentId(permission.getParentId());
        permissionHistory.setPath(permission.getPath());
        permissionHistory.setMethod(permission.getMethod());
        permissionHistory.setSort(permission.getSort());
        permissionHistory.setOperateType(operateType);
        permissionHistory.setOperateTime(permission.getUpdateTime());
        permissionHistory.setOperatorId(permission.getUpdateUserId());

        int result = permissionHistoryMapper.insert(permissionHistory);

        if (result != 1) { // 写入历史失败（应该极少）
            throw new HistoryInsertFailedException(MessageConstant.HISTORY_INSERT_FAILED);
        }
    }
}
