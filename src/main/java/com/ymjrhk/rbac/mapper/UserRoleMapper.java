package com.ymjrhk.rbac.mapper;

import com.ymjrhk.rbac.entity.UserRole;
import com.ymjrhk.rbac.vo.RoleVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;

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
     * 插入关系数据
     * @param relations
     * @return
     */
    int batchInsert(List<UserRole> relations);

    /**
     * 根据 userId 查询用户角色
     * @param userId
     * @return
     */
    List<RoleVO> selectRolesByUserId(Long userId);

    /**
     * 根据 userId 查是否拥有角色 roleName
     * @param userId
     * @param roleName
     * @return
     */
    // 使用boolean代替int
    boolean userHasRole(Long userId, String roleName);
}
