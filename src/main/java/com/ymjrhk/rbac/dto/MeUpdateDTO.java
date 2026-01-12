package com.ymjrhk.rbac.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "个人信息修改参数")
// 不允许修改username、status
// TODO: 未来新增头像
public class MeUpdateDTO {
    private String nickname;

    private String email;
}
