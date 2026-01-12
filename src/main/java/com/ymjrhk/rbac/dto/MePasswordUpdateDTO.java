package com.ymjrhk.rbac.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "个人密码修改参数")
public class MePasswordUpdateDTO {

    private String oldPassword;

    private String newPassword;
}
