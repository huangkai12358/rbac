package com.ymjrhk.rbac.service.impl;

import com.ymjrhk.rbac.entity.User;
import com.ymjrhk.rbac.entity.UserHistory;
import com.ymjrhk.rbac.mapper.UserHistoryMapper;
import com.ymjrhk.rbac.mapper.UserMapper;
import com.ymjrhk.rbac.service.UserHistoryService;
import com.ymjrhk.rbac.service.UserService;
import com.ymjrhk.rbac.service.base.BaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserHistoryServiceImpl implements UserHistoryService {

    private final UserHistoryMapper userHistoryMapper;

    private final UserMapper userMapper;

    @Override
    public void record(Long userId, Integer operateType) {
        User user = userMapper.getByUserId(userId);

        UserHistory userHistory = new UserHistory();

        userHistory.setUserId(userId);
        userHistory.setVersion(user.getVersion());
        userHistory.setUsername(user.getUsername());
        userHistory.setPassword(user.getPassword());
        userHistory.setNickname(user.getNickname());
        userHistory.setEmail(user.getEmail());
        userHistory.setStatus(user.getStatus());
        userHistory.setSecretToken(user.getSecretToken());
        userHistory.setAuthVersion(user.getAuthVersion());
        userHistory.setOperateType(operateType);
        userHistory.setOperateTime(user.getUpdateTime());
        userHistory.setOperatorId(user.getUpdateUserId());

        userHistoryMapper.insert(userHistory);
    }
}
