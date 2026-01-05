package com.ymjrhk.rbac.mapper;

import com.github.pagehelper.Page;
import com.ymjrhk.rbac.dto.PermissionPageQueryDTO;
import com.ymjrhk.rbac.entity.Permission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface PermissionMapper {
    /**
     * 创建权限
     * @param permission
     */
    int insert(Permission permission);

    /**
     * 权限分页查询
     * @param permissionPageQueryDTO
     * @return
     */
    Page<Permission> pageQuery(PermissionPageQueryDTO permissionPageQueryDTO);

    /**
     * 根据 permissionId 查询权限
     * @param permissionId
     * @return
     */
    @Select("select * from sys_permission where permission_id = #{permissionId}")
    Permission getByPermissionId(Long permissionId);

    /**
     * 更新权限
     * @param permission
     * @return
     */
    int update(Permission permission);
}
