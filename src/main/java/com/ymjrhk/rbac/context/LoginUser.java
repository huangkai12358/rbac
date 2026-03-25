package com.ymjrhk.rbac.context;
import java.util.Objects;


/**
 * 当前是谁
 */
public class LoginUser {

    private Long userId;
    private String username;
    public LoginUser(Long userId, String username) {
        this.userId = userId;
        this.username = username;
    }

    public LoginUser() {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        LoginUser that = (LoginUser) o;
        return Objects.equals(userId, that.userId) && Objects.equals(username, that.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, username);
    }

    @Override
    public String toString() {
        return "LoginUser" + "{" + "userId=" + userId + ", " + "username=" + username + "}";
    }

}
