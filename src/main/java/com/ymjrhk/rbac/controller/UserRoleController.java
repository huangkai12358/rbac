package com.ymjrhk.rbac.controller;

import com.ymjrhk.rbac.annotation.Audit;
import com.ymjrhk.rbac.dto.*;
import com.ymjrhk.rbac.result.Result;
import com.ymjrhk.rbac.service.UserRoleService;
import com.ymjrhk.rbac.vo.RoleVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.ymjrhk.rbac.constant.PermissionNameConstant.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "用户-角色管理模块")
public class UserRoleController {

    private final UserRoleService userRoleService;

    /**
     * 用户分配角色
     * @param userId
     * @param roleIdsDTO
     * @return
     */
    @Audit(permission = USER_ASSIGN)
    @PostMapping("/{userId}/roles")
    @Operation(summary = "用户分配角色")
    public Result<Void> assignRolesToUser(@PathVariable("userId") Long userId, @RequestBody @Valid IdsDTO roleIdsDTO) {
        log.info("用户分配角色，userId: {}, roleIds: {}", userId, roleIdsDTO);
        userRoleService.assignRolesToUser(userId, roleIdsDTO.getIds());
        return Result.success();
    }

    /**
     * 查询用户角色
     * @param userId
     * @return
     */
    @Audit(permission = USER_VIEW)
    @GetMapping("/{userId}/roles")
    @Operation(summary = "查询用户角色")
    public Result<List<RoleVO>> getUserRoles(@PathVariable("userId") Long userId) {
        log.info("查询用户角色，userId: {}", userId);
        List<RoleVO> roles = userRoleService.getUserRoles(userId);
        return Result.success(roles);
    }
}
