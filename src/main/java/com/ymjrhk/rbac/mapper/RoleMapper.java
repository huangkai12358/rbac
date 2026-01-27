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
     * 对给定的 roleIds，选出其中在 sys_role 表中实际存在的 roleIds
     *
     * @param roleIds
     * @return
     */
    List<Long> selectExistingRoleIds(List<Long> roleIds);

    /**
     * 对给定的 roleIds，选出其中在 sys_role 表中实际存在并且未禁用的的 roleIds
     * @param roleIds
     * @return
     */
    List<Long> selectEnabledRoleIds(List<Long> roleIds);

    /**
     * 根据 userId 和 role status 查 Role（可查禁用或非禁用）
     * @param userId
     * @param status
     * @return
     */
    List<Role> selectRolesByUserIdAndStatus(Long userId, int status);

    /**
     * 获取所有未禁用的 roleId
     * @return
     */
    List<Long> selectAllEnabledRoleIds();
}
