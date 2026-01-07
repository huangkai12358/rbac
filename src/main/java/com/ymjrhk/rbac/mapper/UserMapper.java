package com.ymjrhk.rbac.mapper;

import com.github.pagehelper.Page;
import com.ymjrhk.rbac.dto.UserPageQueryDTO;
import com.ymjrhk.rbac.entity.User;
import com.ymjrhk.rbac.vo.UserPermissionVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserMapper {
    /**
     * 根据用户名查询用户
     * @param username
     * @return
     */
    @Select("select * from sys_user where username = #{username}")
    User getByUsername(String username);

    /**
     * 创建用户
     * @param user
     */
//    @Insert("insert into sys_user (username, password, nickname, secret_token, create_user_id, update_user_id)" +
//            "values" +
//            "(#{username}, #{password}, #{nickname}, #{secretToken}, #{createUserId}, #{updateUserId})"
//    )
    int insert(User user);

    /**
     * 用户分页查询
     * @param userPageQueryDTO
     * @return
     */
    Page<User> pageQuery(UserPageQueryDTO userPageQueryDTO);

    /**
     * 根据 userId 查询用户
     * @param userId
     * @return
     */
    @Select("select * from sys_user where user_id = #{userId}")
    User getByUserId(Long userId);

    /**
     * 修改用户
     * @param user
     */
    // TODO: 把update分成几个
    int update(User user);

    /**
     * 根据 userId 查询用户权限
     * @param userId
     * @return
     */
    List<UserPermissionVO> selectPermissionsByUserId(Long userId);

}
