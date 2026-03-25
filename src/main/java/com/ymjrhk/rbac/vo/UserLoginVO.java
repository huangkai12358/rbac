package com.ymjrhk.rbac.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Objects;

@Schema(description = "用户登录返回参数")
public class UserLoginVO {
    private Long userId;

    @Schema(description = "用户名", example = "admin")
    private String username;

    private String nickname;

    private String token;

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long userId;
        private String username;
        private String nickname;
        private String token;

        public Builder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder nickname(String nickname) {
            this.nickname = nickname;
            return this;
        }

        public Builder token(String token) {
            this.token = token;
            return this;
        }

        public UserLoginVO build() {
            UserLoginVO vo = new UserLoginVO();
            vo.setUserId(userId);
            vo.setUsername(username);
            vo.setNickname(nickname);
            vo.setToken(token);
            return vo;
        }
    }
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UserLoginVO that = (UserLoginVO) o;
        return Objects.equals(userId, that.userId) && Objects.equals(username, that.username) && Objects.equals(nickname, that.nickname) && Objects.equals(token, that.token) && Objects.equals(userId, that.userId) && Objects.equals(username, that.username) && Objects.equals(nickname, that.nickname) && Objects.equals(token, that.token);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, username, nickname, token, userId, username, nickname, token);
    }

    @Override
    public String toString() {
        return "UserLoginVO" + "{" + "userId=" + userId + ", " + "username=" + username + ", " + "nickname=" + nickname + ", " + "token=" + token + ", " + "userId=" + userId + ", " + "username=" + username + ", " + "nickname=" + nickname + ", " + "token=" + token + "}";
    }

}