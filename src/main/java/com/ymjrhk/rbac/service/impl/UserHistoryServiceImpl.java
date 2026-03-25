package com.ymjrhk.rbac.service.impl;

import com.ymjrhk.rbac.constant.MessageConstant;
import com.ymjrhk.rbac.entity.User;
import com.ymjrhk.rbac.entity.UserHistory;
import com.ymjrhk.rbac.exception.HistoryInsertFailedException;
import com.ymjrhk.rbac.mapper.UserHistoryMapper;
import com.ymjrhk.rbac.mapper.UserMapper;
import com.ymjrhk.rbac.service.UserHistoryService;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 只保存成功的记录
 */
@Service
public class UserHistoryServiceImpl implements UserHistoryService {
    private static final Logger log = LoggerFactory.getLogger(UserHistoryServiceImpl.class);


    private final UserHistoryMapper userHistoryMapper;

    private final UserMapper userMapper;

    /**
     * 记录到历史表
     *
     * @param userId
     * @param operateType
     */
    @Override
    public void recordHistory(Long userId, Integer operateType) {
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
    public UserHistoryServiceImpl(UserHistoryMapper userHistoryMapper, UserMapper userMapper) {
        this.userHistoryMapper = userHistoryMapper;
        this.userMapper = userMapper;
    }

}