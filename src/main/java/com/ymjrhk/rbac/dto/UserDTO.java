package com.ymjrhk.rbac.dto;
import java.util.Objects;


// 修改用
public class UserDTO {
    private String username;

    private String nickname;

    private String email;

    private Integer version;

    private String secretToken;

//    private Integer status; // TODO: 要取消status吗
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

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getSecretToken() {
        return secretToken;
    }

    public void setSecretToken(String secretToken) {
        this.secretToken = secretToken;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UserDTO that = (UserDTO) o;
        return Objects.equals(username, that.username) && Objects.equals(nickname, that.nickname) && Objects.equals(email, that.email) && Objects.equals(version, that.version) && Objects.equals(secretToken, that.secretToken);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, nickname, email, version, secretToken);
    }

    @Override
    public String toString() {
        return "UserDTO" + "{" + "username=" + username + ", " + "nickname=" + nickname + ", " + "email=" + email + ", " + "version=" + version + ", " + "secretToken=" + secretToken + "}";
    }

}
