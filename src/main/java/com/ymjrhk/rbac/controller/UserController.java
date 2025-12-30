package com.ymjrhk.rbac.controller;

import com.ymjrhk.rbac.result.Result;
import com.ymjrhk.rbac.dto.UserLoginDTO;
import com.ymjrhk.rbac.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@Slf4j
@Tag(name = "用户管理")
public class UserController {
    @Autowired
    private UserService userService;

    /**
     * 根据 id 查询用户
     * @param userId
     * @return
     */
    @GetMapping("/{id}")
    @Operation(summary = "根据id查询用户")
    public Result queryUserById(@PathVariable("id") Long userId) {
        return Result.success();
    }

    /**
     * 创建用户
     * @param userLoginDTO
     * @return
     */
    @PostMapping
    @Operation(summary = "创建用户")
    public Result create(@RequestBody UserLoginDTO userLoginDTO) {
        log.info("创建用户：{}", userLoginDTO);
        userService.create(userLoginDTO);
        return Result.success();
    }

}
