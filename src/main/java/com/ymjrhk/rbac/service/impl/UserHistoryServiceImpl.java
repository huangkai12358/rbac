package com.ymjrhk.rbac.service.impl;

import com.ymjrhk.rbac.constant.MessageConstant;
import com.ymjrhk.rbac.entity.User;
import com.ymjrhk.rbac.entity.UserHistory;
import com.ymjrhk.rbac.exception.HistoryInsertFailedException;
import com.ymjrhk.rbac.mapper.UserHistoryMapper;
import com.ymjrhk.rbac.mapper.UserMapper;
import com.ymjrhk.rbac.service.UserHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 只保存成功的记录
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserHistoryServiceImpl implements UserHistoryService {

    private final UserHistoryMapper userHistoryMapper;

    private final UserMapper userMapper;

    /**
     * 记录到历史表
     *
     * @param userId
     * @param operateType
     */
    @Override
    public void record(Long userId, Integer operateType) {
        log.debug("先读出新表所有数据，再拷贝到历史表中：");
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

        int result = userHistoryMapper.insert(userHistory);

        if (result != 1) { // 写入历史失败（应该极少）
            throw new HistoryInsertFailedException(MessageConstant.HISTORY_INSERT_FAILED);
        }

    }
}
