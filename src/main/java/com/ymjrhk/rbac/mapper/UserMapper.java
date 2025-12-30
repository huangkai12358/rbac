package com.ymjrhk.rbac.mapper;

import com.ymjrhk.rbac.dto.UserLoginDTO;
import com.ymjrhk.rbac.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {

    @Insert("insert into sys_user (username, password, nickname, secret_token, create_time, create_user_id, update_time, update_user_id)" +
            "values" +
            "(#{username}, #{password}, #{nickname}, #{secretToken}, #{createTime}, #{createUserId}, #{updateTime}, #{updateUserId})"
    )
    void create(User user);

    /**
     * 根据用户名查询用户
     * @param username
     * @return
     */
    @Select("select * from sys_user where username = #{username}")
    User getByUsername(String username);
}
