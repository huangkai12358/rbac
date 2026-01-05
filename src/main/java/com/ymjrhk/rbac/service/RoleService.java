package com.ymjrhk.rbac.service;

import com.ymjrhk.rbac.dto.*;
import com.ymjrhk.rbac.result.PageResult;
import com.ymjrhk.rbac.vo.RoleVO;
import com.ymjrhk.rbac.vo.UserVO;
import jakarta.validation.Valid;

public interface RoleService {

    void create(@Valid RoleCreateDTO roleCreateDTO);

    PageResult pageQuery(RolePageQueryDTO rolePageQueryDTO);

    RoleVO getByRoleId(Long roleId);

    void update(RoleDTO roleDTO);

    void changeStatus(Long roleId, Integer status);
}
