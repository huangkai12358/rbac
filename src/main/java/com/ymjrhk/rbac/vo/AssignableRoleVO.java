package com.ymjrhk.rbac.vo;

import lombok.Data;

@Data
public class AssignableRoleVO {

    private Long roleId;

    private String roleDisplayName;

    /** 我是否拥有该角色 */
    private boolean ownedByMe;

    /** 用户是否已经拥有该角色 */
    private boolean ownedByUser;
}
