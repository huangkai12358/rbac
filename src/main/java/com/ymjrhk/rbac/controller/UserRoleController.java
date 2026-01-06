package com.ymjrhk.rbac.controller;

import com.ymjrhk.rbac.dto.*;
import com.ymjrhk.rbac.result.Result;
import com.ymjrhk.rbac.service.UserRoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
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
    @PostMapping("/{userId}/roles")
    @Operation(summary = "用户分配角色")
    public Result<Void> userAssignRoles(@PathVariable("userId") Long userId, @RequestBody @Valid IdsDTO roleIdsDTO) {
        log.info("userId: {}, roleIds: {}", userId, roleIdsDTO);
        userRoleService.userAssignRoles(userId, roleIdsDTO.getIds());
        return Result.success();
    }
}
