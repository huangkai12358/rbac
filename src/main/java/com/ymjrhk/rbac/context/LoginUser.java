package com.ymjrhk.rbac.context;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 当前是谁
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginUser {

    private Long userId;
    private String username;
}
