package com.ymjrhk.rbac.service;

import com.ymjrhk.rbac.dto.PermissionCreateDTO;
import com.ymjrhk.rbac.dto.PermissionDTO;
import com.ymjrhk.rbac.dto.PermissionPageQueryDTO;
import com.ymjrhk.rbac.result.PageResult;
import com.ymjrhk.rbac.vo.PermissionVO;

public interface PermissionService {

    Long create(PermissionCreateDTO permissionCreateDTO);

    PageResult pageQuery(PermissionPageQueryDTO permissionPageQueryDTO);

    PermissionVO getByPermissionId(Long permissionId);

    void update(Long permissionId, PermissionDTO permissionDTO);

    void changeStatus(Long permissionId, Integer status);
}
