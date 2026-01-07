package com.ymjrhk.rbac.controller;

import com.ymjrhk.rbac.dto.*;
import com.ymjrhk.rbac.result.PageResult;
import com.ymjrhk.rbac.result.Result;
import com.ymjrhk.rbac.service.UserService;
import com.ymjrhk.rbac.vo.UserPermissionVO;
import com.ymjrhk.rbac.vo.UserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "用户管理模块")
public class UserController {

    private final UserService userService;

    /**
     * 创建用户
     * @param userLoginDTO
     * @return
     */
    @PostMapping
    @Operation(summary = "创建用户")
    public Result<Void> createUser(@RequestBody @Valid UserLoginDTO userLoginDTO) {
        log.info("创建用户：{}", userLoginDTO);
        userService.create(userLoginDTO);
        return Result.success();
    }

    /**
     * 用户分页查询
     * @param userPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    @Operation(summary = "用户分页查询") // TODO: 目前策略是前端不传pageNum和pageSize会报错
    public Result<PageResult> pageQuery(UserPageQueryDTO userPageQueryDTO) {
        log.info("用户分页查询，参数为：{}", userPageQueryDTO);
        PageResult pageResult = userService.pageQuery(userPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 根据 userId 查询用户
     * @param userId
     * @return
     */
    @GetMapping("/{userId}")
    @Operation(summary = "根据 userId 查询用户")
    public Result<UserVO> queryUserById(@PathVariable("userId") Long userId) {
        UserVO userVO = userService.getByUserId(userId);
        return Result.success(userVO);
    }

    /**
     * 修改用户
     * @param userDTO
     * @return
     */
    @PutMapping
    @Operation(summary = "修改用户")
    public Result<Void> updateUser(@RequestBody UserDTO userDTO) {
        log.info("修改用户：{}", userDTO);
        userService.update(userDTO);
        return Result.success();
    }

    /**
     * 启用或禁用用户
     * @param userId
     * @param statusDTO
     * @return
     */
    @PutMapping("/{userId}/status")
    @Operation(summary = "启用或禁用用户")
    public Result<Void> changeStatus(@PathVariable("userId") Long userId, @RequestBody StatusDTO statusDTO) {
        userService.changeStatus(userId, statusDTO.getStatus());
        return Result.success();
    }

    /**
     * 重置用户密码
     * @param userId
     * @return
     */
    @PostMapping("/{userId}/password/reset")
    @Operation(summary = "重置用户密码")
    public Result<Void> resetPassward(@PathVariable("userId") Long userId) {
        userService.resetPassword(userId);
        return Result.success();
    }

    @GetMapping("/{userId}/permissions")
    @Operation(summary = "查询用户权限（非禁用）")
    public Result<List<UserPermissionVO>> getUserPermissions(@PathVariable("userId") Long userId) {
        log.info("查询用户权限，userId: {}", userId);
        List<UserPermissionVO> permissions = userService.getUserPermissions(userId);
        return Result.success(permissions);
    }
}
