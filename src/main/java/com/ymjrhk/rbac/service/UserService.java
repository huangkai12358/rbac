package com.ymjrhk.rbac.service;

import com.ymjrhk.rbac.dto.UserDTO;
import com.ymjrhk.rbac.dto.UserLoginDTO;
import com.ymjrhk.rbac.dto.UserPageQueryDTO;
import com.ymjrhk.rbac.result.PageResult;
import com.ymjrhk.rbac.vo.UserVO;

public interface UserService {
    void create(UserLoginDTO userLoginDTO);

    PageResult pageQuery(UserPageQueryDTO userPageQueryDTO);

    UserVO getByUserId(Long userId);

    void update(UserDTO userDTO);

    void changeStatus(Long userId, Integer status);

    void resetPassword(Long userId);
}
