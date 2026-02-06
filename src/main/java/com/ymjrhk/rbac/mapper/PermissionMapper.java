package com.ymjrhk.rbac.mapper;

import com.github.pagehelper.Page;
import com.ymjrhk.rbac.dto.PermissionPageQueryDTO;
import com.ymjrhk.rbac.entity.Permission;
import com.ymjrhk.rbac.vo.PermissionVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface PermissionMapper {
    /**
     * 创建权限
     *
     * @param permission
     */
    int insert(Permission permission);

    /**
     * 权限分页查询
     *
     * @param permissionPageQueryDTO
     * @return
     */
    Page<Permission> pageQuery(PermissionPageQueryDTO permissionPageQueryDTO);

    /**
     * 权限分页查询（只查 ID）
     *
     * @param dto
     * @return
     */
    List<Long> pageQueryIds(PermissionPageQueryDTO dto);

    /**
     * 按 ID 查完整数据
     *
     * @param ids
     * @return
     */
    List<Permission> selectByIds(@Param("ids") List<Long> ids);

    /**
     * 查符合条件权限总数
     *
     * @param dto
     * @return
     */
    long count(PermissionPageQueryDTO dto);

    /**
     * 根据 permissionId 查询权限
     *
     * @param permissionId
     * @return
     */
    @Select("select * from sys_permission where permission_id = #{permissionId}")
    Permission getByPermissionId(Long permissionId);

    /**
     * 更新权限
     *
     * @param permission
     * @return
     */
    int update(Permission permission);

    /**
     * 选出在 sys_permission 表中实际存在的 permissionIds
     *
     * @param permissionIds
     * @return
     */
    List<Long> selectExistingPermissionIds(List<Long> permissionIds);

    /**
     * 查询 sys_permission 表中所有未禁用的权限
     *
     * @return
     */
    @Select("select permission_id, permission_name, permission_display_name, description, status, type, parent_id, path, method, sort " +
            "from sys_permission where status = 1 and type = 2")
    List<PermissionVO> listAllActivePermissions();

    /**
     * 对给定的 permissionIds，选出其中在 sys_permission 表中实际存在并且未禁用的的 permissionIds
     *
     * @param permissionIds
     * @return
     */
    List<Long> selectEnabledPermissionIds(List<Long> permissionIds);

    /**
     * 获取所有未禁用的 permissionId
     *
     * @return
     */
    List<Long> selectAllEnabledPermissionIds();

    /**
     * 按条件查询所有权限
     *
     * @param dto
     * @return
     */
    List<PermissionVO> listForExport(PermissionPageQueryDTO dto);
}
