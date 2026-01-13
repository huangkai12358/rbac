package com.ymjrhk.rbac.mapper;

import com.ymjrhk.rbac.entity.PermissionHistory;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PermissionHistoryMapper {

    /**
     * 记录到历史表
     *
     * @param permissionHistory
     * @return
     */
    @Insert("insert into sys_permission_history (permission_id, version, permission_name, permission_display_name, description, status, secret_token, type, parent_id, path, method, sort, operate_type, operate_time, operator_id)" +
            "values " +
            "(#{permissionId}, #{version}, #{permissionName}, #{permissionDisplayName}, #{description}, #{status}, #{secretToken}, #{type}, #{parentId}, #{path}, #{method}, #{sort}, #{operateType}, #{operateTime}, #{operatorId})"
    )
    int insert(PermissionHistory permissionHistory);
}
