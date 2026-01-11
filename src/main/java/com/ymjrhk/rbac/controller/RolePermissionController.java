package com.ymjrhk.rbac.controller;

import com.ymjrhk.rbac.dto.IdsDTO;
import com.ymjrhk.rbac.result.Result;
import com.ymjrhk.rbac.service.RolePermissionService;
import com.ymjrhk.rbac.vo.PermissionVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
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
    public Result<Void> assignPermissionsToRole(@PathVariable("roleId") Long roleId, @RequestBody @Valid IdsDTO permissionIdsDTO) {
        log.info("roleId: {}, permissionIds: {}", roleId, permissionIdsDTO);
        rolePermissionService.assignPermissionsToRole(roleId, permissionIdsDTO.getIds());
        return Result.success();
    }

    @GetMapping("/{roleId}/permissions")
    @Operation(summary = "查询角色权限")
    public Result<List<PermissionVO>> getRolePermissions(@PathVariable("roleId") Long roleId) {
        log.info("查询角色权限，roleId: {}", roleId);
        List<PermissionVO> permissions = rolePermissionService.getRolePermissions(roleId);
        return Result.success(permissions);
    }
    
}
