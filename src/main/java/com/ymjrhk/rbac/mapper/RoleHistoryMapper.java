package com.ymjrhk.rbac.mapper;

import com.ymjrhk.rbac.entity.RoleHistory;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RoleHistoryMapper {

    /**
     * 记录到历史表
     *
     * @param roleHistory
     * @return
     */
    @Insert("insert into sys_role_history (role_id, version, role_name, role_display_name, description, status, secret_token, operate_type, operate_time, operator_id)" +
            "values " +
            "(#{roleId}, #{version}, #{roleName}, #{roleDisplayName}, #{description}, #{status}, #{secretToken}, #{operateType}, #{operateTime}, #{operatorId})"
    )
    int insert(RoleHistory roleHistory);
}
