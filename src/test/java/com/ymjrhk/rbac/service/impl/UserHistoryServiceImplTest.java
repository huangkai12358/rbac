package com.ymjrhk.rbac.service.impl;

import com.ymjrhk.rbac.constant.OperateTypeConstant;
import com.ymjrhk.rbac.constant.StatusConstant;
import com.ymjrhk.rbac.entity.User;
import com.ymjrhk.rbac.entity.UserHistory;
import com.ymjrhk.rbac.exception.HistoryInsertFailedException;
import com.ymjrhk.rbac.mapper.UserHistoryMapper;
import com.ymjrhk.rbac.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserHistoryServiceImplTest {

    @InjectMocks
    private UserHistoryServiceImpl userHistoryService;

    @Mock
    private UserHistoryMapper userHistoryMapper;

    @Mock
    private UserMapper userMapper;

    // ========================= recordHistory() =========================

    /**
     * 正常写入历史表
     */
    @Test
    void recordHistory_success() {
        // given
        Long userId = 1L;
        Integer operateType = OperateTypeConstant.CREATE;

        User user = new User();
        user.setUserId(userId);
        user.setVersion(3);
        user.setUsername("zhangsan");
        user.setPassword("ENCODED");
        user.setNickname("张三");
        user.setEmail("zs@test.com");
        user.setStatus(StatusConstant.ENABLED);
        user.setSecretToken("secret");
        user.setAuthVersion(2);
        user.setUpdateUserId(100L);
        user.setUpdateTime(LocalDateTime.now());

        when(userMapper.getByUserId(userId))
                .thenReturn(user);
        when(userHistoryMapper.insert(any(UserHistory.class)))
                .thenReturn(1);

        // when
        userHistoryService.recordHistory(userId, operateType);

        // then
        verify(userMapper).getByUserId(userId);

        verify(userHistoryMapper).insert(argThat(history ->
                history.getUserId().equals(userId)
                        && history.getVersion().equals(3)
                        && history.getUsername().equals("zhangsan")
                        && history.getPassword().equals("ENCODED")
                        && history.getNickname().equals("张三")
                        && history.getEmail().equals("zs@test.com")
                        && history.getStatus().equals(StatusConstant.ENABLED)
                        && history.getSecretToken().equals("secret")
                        && history.getAuthVersion().equals(2)
                        && history.getOperateType().equals(operateType)
                        && history.getOperatorId().equals(100L)
        ));
    }

    /**
     * 历史表写入失败
     */
    @Test
    void recordHistory_insertFail_throwException() {
        // given
        Long userId = 1L;
        Integer operateType = OperateTypeConstant.UPDATE;

        User user = new User();
        user.setUserId(userId);
        user.setVersion(1);
        user.setUsername("zhangsan");

        when(userMapper.getByUserId(userId))
                .thenReturn(user);
        when(userHistoryMapper.insert(any(UserHistory.class)))
                .thenReturn(0); // 模拟插入失败

        // then
        assertThrows(HistoryInsertFailedException.class,
                () -> userHistoryService.recordHistory(userId, operateType));
    }

}
