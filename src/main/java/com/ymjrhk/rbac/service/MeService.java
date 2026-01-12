package com.ymjrhk.rbac.service;

import com.ymjrhk.rbac.dto.MePasswordUpdateDTO;
import com.ymjrhk.rbac.dto.MeUpdateDTO;
import com.ymjrhk.rbac.vo.MeViewVO;

public interface MeService {
    MeViewVO query();

    void update(MeUpdateDTO meUpdateDTO);

    void changePassword(MePasswordUpdateDTO mePasswordUpdateDTO);
}
