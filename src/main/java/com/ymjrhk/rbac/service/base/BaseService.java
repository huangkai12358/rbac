package com.ymjrhk.rbac.service.base;

import com.ymjrhk.rbac.context.BaseContext;
import com.ymjrhk.rbac.entity.OptimisticLockEntity;
import com.ymjrhk.rbac.exception.StatusNotChangeException;

import java.util.Objects;
import java.util.UUID;

import static com.ymjrhk.rbac.constant.MessageConstant.STATUS_NOT_CHANGE;

public abstract class BaseService {

    /**
     * 用户/角色/权限 的乐观锁字段（包括 updateUserId）公共填充方法
     *
     * @param entity
     * @param version
     * @param oldSecretToken
     * @param newSecretToken
     * @param updateUserId
     * @param <T>
     */
    protected <T extends OptimisticLockEntity> void fillOptimisticLockFields(
            T entity,
            Integer version,
            String oldSecretToken,
            String newSecretToken,
            Long updateUserId) {

        entity.setVersion(version);
        entity.setSecretToken(oldSecretToken);
        entity.setNewSecretToken(newSecretToken);
        entity.setUpdateUserId(updateUserId);
    }

    /**
     * 启用或禁用 用户/角色/权限 的公共方法
     *
     * @param dbEntity
     * @param updateEntity
     * @param newStatus
     */
    protected void changeStatus(
            OptimisticLockEntity dbEntity,
            OptimisticLockEntity updateEntity,
            Integer newStatus
    ) {
        // 状态未改变，无需更改
        if (Objects.equals(dbEntity.getStatus(), newStatus)) {
            throw new StatusNotChangeException(STATUS_NOT_CHANGE);
        }

        // 设置新状态
        updateEntity.setStatus(newStatus);

        Integer version = dbEntity.getVersion(); // 获取版本号
        String secretToken = dbEntity.getSecretToken(); // 获取旧 secretToken
        String newSecretToken = UUID.randomUUID().toString();
        Long updateUserId = BaseContext.getCurrentUserId();

        // 乐观锁字段填充
        fillOptimisticLockFields(
                updateEntity,
                version,
                secretToken,
                newSecretToken,
                updateUserId
        );
    }

}
