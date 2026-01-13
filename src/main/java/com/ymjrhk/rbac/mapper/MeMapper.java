package com.ymjrhk.rbac.mapper;

import com.ymjrhk.rbac.vo.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface MeMapper {
    /**
     * 查询个人信息
     *
     * @param userId
     * @return
     */
    @Select("select user_id, username, nickname, email, create_time from sys_user where user_id = #{userId}")
    MeViewVO getByUserId(Long userId);

    List<MeRoleVO> selectRolesByUserId(Long userId);

    List<MePermissionVO> selectPermissionsByUserId(Long userId);
}
