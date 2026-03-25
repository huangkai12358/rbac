package com.ymjrhk.rbac.dto;

import com.alibaba.fastjson2.annotation.JSONField;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import java.util.Objects;

@Schema(description = "个人密码修改参数")
public class MePasswordUpdateDTO {

    @Schema(description = "原密码", example = "123456")
    @NotBlank(message = "原密码不能为空")
    @JSONField(ordinal = 1) // Fastjson 注解指定反序列化顺序
    private String oldPassword;

    @Schema(description = "新密码", example = "12345678")
    @NotBlank(message = "新密码不能为空")
    @JSONField(ordinal = 2)
    private String newPassword;
    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MePasswordUpdateDTO that = (MePasswordUpdateDTO) o;
        return Objects.equals(oldPassword, that.oldPassword) && Objects.equals(newPassword, that.newPassword);
    }

    @Override
    public int hashCode() {
        return Objects.hash(oldPassword, newPassword);
    }

    @Override
    public String toString() {
        return "MePasswordUpdateDTO" + "{" + "oldPassword=" + oldPassword + ", " + "newPassword=" + newPassword + "}";
    }

}
