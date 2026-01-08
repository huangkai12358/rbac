package com.ymjrhk.rbac.mapper;

import com.ymjrhk.rbac.entity.UserHistory;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserHistoryMapper {

    @Insert("insert into sys_user_history (user_id, version, username, password, nickname, email, status, secret_token, auth_version, operate_type, operate_time, operator_id)" +
            "values " +
            "(#{userId}, #{version}, #{username}, #{password}, #{nickname}, #{email}, #{status}, #{secretToken}, #{authVersion}, #{operateType}, #{operateTime}, #{operatorId})"
    )
    int insert(UserHistory userHistory);
}
