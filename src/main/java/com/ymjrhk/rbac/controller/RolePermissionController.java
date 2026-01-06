package com.ymjrhk.rbac.controller;

import com.ymjrhk.rbac.dto.IdsDTO;
import com.ymjrhk.rbac.result.Result;
import com.ymjrhk.rbac.service.RolePermissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/role")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "角色-权限管理模块")
public class RolePermissionController {
    
    private final RolePermissionService rolePermissionService;

    /**
     * 角色分配权限
     * @param roleId
     * @param permissionIdsDTO
     * @return
     */
    @PostMapping("/{roleId}/permissions")
    @Operation(summary = "角色分配权限")
    public Result<Void> roleAssignPermissions(@PathVariable("roleId") Long roleId, @RequestBody @Valid IdsDTO permissionIdsDTO) {
        log.info("roleId: {}, permissionIds: {}", roleId, permissionIdsDTO);
        rolePermissionService.roleAssignPermissions(roleId, permissionIdsDTO.getIds());
        return Result.success();
    }
}
