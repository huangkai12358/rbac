package com.ymjrhk.rbac.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class UserHistory implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long userId;

    private Integer version;

    private String username;

    private String password;

    private String nickname;

    private String email;

    private Integer status;

    private String secretToken;

    private Integer authVersion;

    private Integer operateType;

    private LocalDateTime operateTime;

    private Long operatorId;
}
