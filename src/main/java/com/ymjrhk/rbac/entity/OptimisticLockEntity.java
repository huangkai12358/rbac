package com.ymjrhk.rbac.entity;

public interface OptimisticLockEntity {
    Integer getStatus();

    Integer getVersion();

    String getSecretToken();

    void setStatus(Integer status);

    void setVersion(Integer version);

    void setSecretToken(String secretToken);

    void setNewSecretToken(String newSecretToken);

    void setUpdateUserId(Long updateUserId);
}
