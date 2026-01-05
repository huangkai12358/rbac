package com.ymjrhk.rbac.service;

import com.ymjrhk.rbac.dto.UserLoginDTO;
import com.ymjrhk.rbac.vo.UserLoginVO;

public interface AuthService {
    UserLoginVO login(UserLoginDTO userLoginDTO);
}
