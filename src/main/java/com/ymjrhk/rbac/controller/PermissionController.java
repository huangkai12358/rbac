package com.ymjrhk.rbac.controller;

import com.ymjrhk.rbac.dto.PermissionCreateDTO;
import com.ymjrhk.rbac.dto.PermissionDTO;
import com.ymjrhk.rbac.dto.PermissionPageQueryDTO;
import com.ymjrhk.rbac.dto.StatusDTO;
import com.ymjrhk.rbac.result.PageResult;
import com.ymjrhk.rbac.result.Result;
import com.ymjrhk.rbac.service.PermissionService;
import com.ymjrhk.rbac.vo.PermissionVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/permission")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "权限管理模块")
public class PermissionController {
    private final PermissionService permissionService;

    /**
     * 创建权限
     * @param permissionCreateDTO
     * @return
     */
    @PostMapping
    @Operation(summary = "创建权限")
    public Result<Void> createPermission(@RequestBody @Valid PermissionCreateDTO permissionCreateDTO) {
        log.info("创建权限：{}", permissionCreateDTO);
        permissionService.create(permissionCreateDTO);
        return Result.success();
    }

    /**
     * 权限分页查询
     * @param permissionPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    @Operation(summary = "权限分页查询")
    public Result<PageResult> pageQuery(PermissionPageQueryDTO permissionPageQueryDTO) {
        log.info("权限分页查询，参数为：{}", permissionPageQueryDTO);
        PageResult pageResult = permissionService.pageQuery(permissionPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 根据 permissionId 查询权限
     * @param permissionId
     * @return
     */
    @GetMapping("/{permissionId}")
    @Operation(summary = "根据 permissionId 查询权限")
    public Result<PermissionVO> queryPermissionById(@PathVariable("permissionId") Long permissionId) {
        PermissionVO permissionVO = permissionService.getByPermissionId(permissionId);
        return Result.success(permissionVO);
    }

    /**
     * 修改权限
     * @param permissionId
     * @param permissionDTO
     * @return
     */
    @PutMapping("/{permissionId}")
    @Operation(summary = "修改权限")
    public Result<Void> updatePermission(@PathVariable Long permissionId, @RequestBody PermissionDTO permissionDTO) {
        log.info("修改权限：permissionId: {}，permissionDTO: {}", permissionId, permissionDTO);
        permissionService.update(permissionId, permissionDTO);
        return Result.success();
    }

    /**
     * 启用或禁用权限
     * @param permissionId
     * @param statusDTO
     * @return
     */
    @PutMapping("/{permissionId}/status")
    @Operation(summary = "启用或禁用权限")
    public Result<Void> changeStatus(@PathVariable("permissionId") Long permissionId, @RequestBody StatusDTO statusDTO) {
        permissionService.changeStatus(permissionId, statusDTO.getStatus());
        return Result.success();
    }
}
