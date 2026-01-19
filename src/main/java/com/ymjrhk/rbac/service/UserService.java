package com.ymjrhk.rbac.service;

import com.ymjrhk.rbac.dto.UserCreateDTO;
import com.ymjrhk.rbac.dto.UserDTO;
import com.ymjrhk.rbac.dto.UserPageQueryDTO;
import com.ymjrhk.rbac.dto.auth.UserAuthInfo;
import com.ymjrhk.rbac.result.PageResult;
import com.ymjrhk.rbac.vo.PermissionVO;
import com.ymjrhk.rbac.vo.UserVO;

import java.util.List;

public interface UserService {
    Long create(UserCreateDTO userCreateDTO);

    PageResult pageQuery(UserPageQueryDTO userPageQueryDTO);

    UserVO getByUserId(Long userId);

    void update(Long userId, UserDTO userDTO);

    void changeStatus(Long userId, Integer status);

    void resetPassword(Long userId);

    List<PermissionVO> getUserPermissions(Long userId);

    boolean hasPermission(Long userId, String path, String method);

    UserAuthInfo getUserAuthInfo(Long userId);

    void incrementAuthVersion(Long userId);
}
