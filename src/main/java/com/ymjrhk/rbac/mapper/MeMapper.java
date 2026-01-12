package com.ymjrhk.rbac.mapper;

import com.ymjrhk.rbac.vo.MeViewVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface MeMapper {
    /**
     * 查询个人信息
     * @param userId
     * @return
     */
    @Select("select user_id, username, nickname, email, create_time from sys_user where user_id = #{userId}")
    MeViewVO getByUserId(Long userId);
}
