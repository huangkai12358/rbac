package com.ymjrhk.rbac.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Objects;

@Schema(description = "个人信息修改参数")
// 不允许修改username、status
// TODO: 未来新增头像
public class MeUpdateDTO {
    private String nickname;

    private String email;
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
        MeUpdateDTO that = (MeUpdateDTO) o;
        return Objects.equals(nickname, that.nickname) && Objects.equals(email, that.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nickname, email);
    }

    @Override
    public String toString() {
        return "MeUpdateDTO" + "{" + "nickname=" + nickname + ", " + "email=" + email + "}";
    }

}
