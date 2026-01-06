package com.ymjrhk.rbac.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserRoleMapper {

    /**
     * 根据 userId 从 sys_user_role 表中删除数据
     * @param userId
     */
    @Delete("delete from sys_user_role where user_id = #{userId}")
    void deleteByUserId(Long userId);

    /**
     * 批量插入数据
     *
     * @param userId
     * @param roleIds
     * @return
     */
    int batchInsert(@Param("userId") Long userId,
                    @Param("roleIds") List<Long> roleIds,
                    @Param("operateUserId") Long operateUserId);
}
