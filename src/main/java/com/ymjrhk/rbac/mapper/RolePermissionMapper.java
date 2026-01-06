package com.ymjrhk.rbac.mapper;

import com.ymjrhk.rbac.entity.RolePermission;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface RolePermissionMapper {

    /**
     * 根据 roleId 从 sys_role_permission 表中删除数据
     * @param roleId
     */
    @Delete("delete from sys_role_permission where role_id = #{roleId}")
    void deleteByRoleId(Long roleId);

    /**
     * 插入关系数据
     * @param relations
     * @return
     */
    int batchInsert(List<RolePermission> relations);
}
