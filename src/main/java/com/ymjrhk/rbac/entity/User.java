package com.ymjrhk.rbac.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long userId;

    private String username;

    private String password;

    private String nickname;

    private String email;

    private Integer status;

    private Integer version;

    private Integer authVersion;

    private String secretToken;

    private LocalDateTime createTime;

    private Long createUserId;

    private LocalDateTime updateTime;

    private Long updateUserId;
}
