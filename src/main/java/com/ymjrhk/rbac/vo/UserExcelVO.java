package com.ymjrhk.rbac.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.Objects;

@Schema(description = "用户导出参数")
public class UserExcelVO {

    @ExcelProperty(value = "ID", index = 0)
    @ColumnWidth(10)
    private Long userId;

    @ExcelProperty(value = "用户名", index = 1)
    @ColumnWidth(20)
    private String username;

    @ExcelProperty(value = "昵称", index = 2)
    @ColumnWidth(20)
    private String nickname;

    @ExcelProperty(value = "邮箱", index = 3)
    @ColumnWidth(30)
    private String email;

    @ExcelProperty(value = "状态", index = 4)
    @ColumnWidth(10)
    private String status; // 注意此处 status 为 String

    @ExcelProperty(value = "创建时间", index = 5)
    @ColumnWidth(25)
    private LocalDateTime createTime;

    @ExcelProperty(value = "修改时间", index = 6)
    @ColumnWidth(25)
    private LocalDateTime updateTime;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UserExcelVO that = (UserExcelVO) o;
        return Objects.equals(userId, that.userId) && Objects.equals(username, that.username) && Objects.equals(nickname, that.nickname) && Objects.equals(email, that.email) && Objects.equals(createTime, that.createTime) && Objects.equals(updateTime, that.updateTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, username, nickname, email, createTime, updateTime);
    }

    @Override
    public String toString() {
        return "UserExcelVO" + "{" + "userId=" + userId + ", " + "username=" + username + ", " + "nickname=" + nickname + ", " + "email=" + email + ", " + "createTime=" + createTime + ", " + "updateTime=" + updateTime + "}";
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}