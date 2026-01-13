package com.ymjrhk.rbac.service;

import com.ymjrhk.rbac.dto.RoleCreateDTO;
import com.ymjrhk.rbac.dto.RoleDTO;
import com.ymjrhk.rbac.dto.RolePageQueryDTO;
import com.ymjrhk.rbac.result.PageResult;
import com.ymjrhk.rbac.vo.RoleVO;

public interface RoleService {

    Long create(RoleCreateDTO roleCreateDTO);

    PageResult pageQuery(RolePageQueryDTO rolePageQueryDTO);

    RoleVO getByRoleId(Long roleId);

    void update(Long roleId, RoleDTO roleDTO);

    void changeStatus(Long roleId, Integer status);
}
