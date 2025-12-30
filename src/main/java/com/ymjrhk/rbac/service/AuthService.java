package com.ymjrhk.rbac.service;

import com.ymjrhk.rbac.dto.UserLoginDTO;
import com.ymjrhk.rbac.entity.User;

public interface AuthService {
    User login(UserLoginDTO userLoginDTO);
}
