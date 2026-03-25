package com.ymjrhk.rbac.controller;

import com.alibaba.excel.EasyExcelFactory;
import com.ymjrhk.rbac.annotation.Audit;
import com.ymjrhk.rbac.dto.StatusDTO;
import com.ymjrhk.rbac.dto.UserCreateDTO;
import com.ymjrhk.rbac.dto.UserDTO;
import com.ymjrhk.rbac.dto.UserPageQueryDTO;
import com.ymjrhk.rbac.result.PageResult;
import com.ymjrhk.rbac.result.Result;
import com.ymjrhk.rbac.service.UserService;
import com.ymjrhk.rbac.utils.ExcelStyleUtil;
import com.ymjrhk.rbac.vo.PermissionVO;
import com.ymjrhk.rbac.vo.UserExcelVO;
import com.ymjrhk.rbac.vo.UserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

import static com.ymjrhk.rbac.constant.PermissionNameConstant.*;

@RestController
@RequestMapping("/api/users")
@Tag(name = "用户管理模块")
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);


    private final UserService userService;

    /**
     * 创建用户
     *
     * @param userCreateDTO
     * @return
     */
    @Audit(permission = USER_CREATE)
    @PostMapping
    @Operation(summary = "创建用户")
    // 注意：“请求体反序列化失败（JSON为空） / 参数校验失败（用户名或密码不存在）”发生在 Controller 方法执行之前，AOP 根本没接管到，所以不会记审计日志。
    public Result<Long> createUser(@RequestBody @Valid UserCreateDTO userCreateDTO) {
        log.info("创建用户：{}", userCreateDTO);
        Long userId = userService.create(userCreateDTO);
        return Result.success(userId);
    }

    /**
     * 用户分页查询
     *
     * @param userPageQueryDTO
     * @return
     */
    @Audit(permission = USER_VIEW)
    @GetMapping("/page")
    @Operation(summary = "用户分页查询")
    public Result<PageResult> pageQuery(UserPageQueryDTO userPageQueryDTO) {
        log.info("用户分页查询，参数为：{}", userPageQueryDTO);
        PageResult pageResult = userService.pageQuery(userPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 根据 userId 查询用户
     *
     * @param userId
     * @return
     */
    @Audit(permission = USER_VIEW)
    @GetMapping("/{userId}")
    @Operation(summary = "根据 userId 查询用户")
    public Result<UserVO> queryUserById(@PathVariable("userId") Long userId) {
        UserVO userVO = userService.getByUserId(userId);
        return Result.success(userVO);
    }

    /**
     * 启用或禁用用户
     *
     * @param userId
     * @param statusDTO
     * @return
     */
    @Audit(permission = USER_STATUS)
    @PutMapping("/{userId}/status")
    @Operation(summary = "启用或禁用用户")
    public Result<Void> changeStatus(@PathVariable("userId") Long userId, @RequestBody StatusDTO statusDTO) {
        userService.changeStatus(userId, statusDTO.getStatus());
        return Result.success();
    }

    /**
     * 修改用户
     *
     * @param userId
     * @param userDTO
     * @return
     */
    @Audit(permission = USER_UPDATE)
    @PutMapping("/{userId}")
    @Operation(summary = "修改用户")
    public Result<Void> updateUser(@PathVariable Long userId, @RequestBody UserDTO userDTO) {
        log.info("修改用户：userId: {}，userDTO: {}", userId, userDTO);
        userService.update(userId, userDTO);
        return Result.success();
    }

    /**
     * 重置用户密码
     *
     * @param userId
     * @return
     */
    @Audit(permission = USER_PASSWORD_RESET)
    @PostMapping("/{userId}/password/reset")
    @Operation(summary = "重置用户密码")
    public Result<Void> resetPassword(@PathVariable("userId") Long userId) {
        userService.resetPassword(userId);
        return Result.success();
    }

    /**
     * 查询用户权限（非禁用角色和权限）
     *
     * @param userId
     * @return
     */
    @Audit(permission = USER_VIEW)
    @GetMapping("/{userId}/permissions")
    @Operation(summary = "查询用户权限（非禁用）")
    public Result<List<PermissionVO>> getUserPermissions(@PathVariable("userId") Long userId) {
        log.info("查询用户权限，userId: {}", userId);
        List<PermissionVO> permissions = userService.getUserPermissions(userId);
        return Result.success(permissions);
    }

    @Audit(permission = USER_VIEW)
    @GetMapping("/export")
    @Operation(summary = "用户数据导出")
    public void export(UserPageQueryDTO dto, HttpServletResponse response) {

        List<UserExcelVO> data = userService.listForExport(dto);

        try {
            response.setContentType(
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");

            EasyExcelFactory.write(response.getOutputStream(), UserExcelVO.class)
                            .registerWriteHandler(ExcelStyleUtil.defaultStyle())
                            .sheet("用户数据")
                            .doWrite(data);

        } catch (IOException e) {
            throw new RuntimeException("导出 Excel 失败", e);
        }
    }
    public UserController(UserService userService) {
        this.userService = userService;
    }

}