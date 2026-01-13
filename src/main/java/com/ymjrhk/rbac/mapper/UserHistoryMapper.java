package com.ymjrhk.rbac.mapper;

import com.ymjrhk.rbac.entity.UserHistory;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserHistoryMapper {

    /**
     * 记录到历史表
     *
     * @param userHistory
     * @return
     */
    @Insert("insert into sys_user_history (user_id, version, username, password, nickname, email, status, secret_token, auth_version, operate_type, operate_time, operator_id)" +
            "values " +
            "(#{userId}, #{version}, #{username}, #{password}, #{nickname}, #{email}, #{status}, #{secretToken}, #{authVersion}, #{operateType}, #{operateTime}, #{operatorId})"
    )
    int insert(UserHistory userHistory);
}
