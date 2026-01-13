package com.ymjrhk.rbac.mapper;

import com.github.pagehelper.Page;
import com.ymjrhk.rbac.dto.RolePageQueryDTO;
import com.ymjrhk.rbac.entity.Role;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface RoleMapper {
    /**
     * 创建角色
     *
     * @param role
     */
    int insert(Role role);

    /**
     * 角色分页查询
     *
     * @param rolePageQueryDTO
     * @return
     */
    Page<Role> pageQuery(RolePageQueryDTO rolePageQueryDTO);

    /**
     * 根据 roleId 查询角色
     *
     * @param roleId
     * @return
     */
    @Select("select * from sys_role where role_id = #{roleId}")
    Role getByRoleId(Long roleId);

    /**
     * 更新角色
     *
     * @param role
     * @return
     */
    int update(Role role);

    /**
     * 选出在 sys_role 表中实际存在的 roleIds
     *
     * @param roleIds
     * @return
     */
    List<Long> selectExistingRoleIds(List<Long> roleIds);
}
