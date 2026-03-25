package com.ymjrhk.rbac.dto;

import com.alibaba.fastjson2.annotation.JSONField;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import java.util.Objects;

@Schema(description = "用户创建参数")
public class UserCreateDTO {
    @Schema(description = "用户名", example = "admin")
    @NotBlank(message = "用户名不能为空")
    @JSONField(ordinal = 1) // Fastjson 注解指定反序列化顺序
    private String username;

    @Schema(description = "昵称", example = "至尊宝之泪")
    @JSONField(ordinal = 2)
    private String nickname;

    @Schema(description = "邮箱", example = "111@qq.com")
    @JSONField(ordinal = 3)
    private String email;
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UserCreateDTO that = (UserCreateDTO) o;
        return Objects.equals(username, that.username) && Objects.equals(nickname, that.nickname) && Objects.equals(email, that.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, nickname, email);
    }

    @Override
    public String toString() {
        return "UserCreateDTO" + "{" + "username=" + username + ", " + "nickname=" + nickname + ", " + "email=" + email + "}";
    }

}
