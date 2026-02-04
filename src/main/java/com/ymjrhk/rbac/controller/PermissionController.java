package com.ymjrhk.rbac.controller;

import com.alibaba.excel.EasyExcelFactory;
import com.ymjrhk.rbac.annotation.Audit;
import com.ymjrhk.rbac.dto.PermissionCreateDTO;
import com.ymjrhk.rbac.dto.PermissionDTO;
import com.ymjrhk.rbac.dto.PermissionPageQueryDTO;
import com.ymjrhk.rbac.dto.StatusDTO;
import com.ymjrhk.rbac.result.PageResult;
import com.ymjrhk.rbac.result.Result;
import com.ymjrhk.rbac.service.PermissionService;
import com.ymjrhk.rbac.utils.ExcelStyleUtil;
import com.ymjrhk.rbac.vo.PermissionExcelVO;
import com.ymjrhk.rbac.vo.PermissionVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

import static com.ymjrhk.rbac.constant.PermissionNameConstant.*;

@RestController
@RequestMapping("/api/permissions")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "权限管理模块")
public class PermissionController {
    private final PermissionService permissionService;

    /**
     * 创建权限
     *
     * @param permissionCreateDTO
     * @return
     */
    @Audit(permission = PERMISSION_CREATE)
    @PostMapping
    @Operation(summary = "创建权限")
    public Result<Long> createPermission(@RequestBody @Valid PermissionCreateDTO permissionCreateDTO) {
        log.info("创建权限：{}", permissionCreateDTO);
        Long permissionId = permissionService.create(permissionCreateDTO);
        return Result.success(permissionId);
    }

    /**
     * 权限分页查询
     *
     * @param permissionPageQueryDTO
     * @return
     */
    @Audit(permission = PERMISSION_VIEW)
    @GetMapping("/page")
    @Operation(summary = "权限分页查询")
    public Result<PageResult> pageQuery(PermissionPageQueryDTO permissionPageQueryDTO) {
        log.info("权限分页查询，参数为：{}", permissionPageQueryDTO);
        PageResult pageResult = permissionService.pageQuery(permissionPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 根据 permissionId 查询权限
     *
     * @param permissionId
     * @return
     */
    @Audit(permission = PERMISSION_VIEW)
    @GetMapping("/{permissionId}")
    @Operation(summary = "根据 permissionId 查询权限")
    public Result<PermissionVO> queryPermissionById(@PathVariable("permissionId") Long permissionId) {
        PermissionVO permissionVO = permissionService.getByPermissionId(permissionId);
        return Result.success(permissionVO);
    }

    /**
     * 修改权限
     *
     * @param permissionId
     * @param permissionDTO
     * @return
     */
    @Audit(permission = PERMISSION_UPDATE)
    @PutMapping("/{permissionId}")
    @Operation(summary = "修改权限")
    public Result<Void> updatePermission(@PathVariable Long permissionId, @RequestBody PermissionDTO permissionDTO) {
        log.info("修改权限：permissionId: {}，permissionDTO: {}", permissionId, permissionDTO);
        permissionService.update(permissionId, permissionDTO);
        return Result.success();
    }

    /**
     * 启用或禁用权限
     *
     * @param permissionId
     * @param statusDTO
     * @return
     */
    @Audit(permission = PERMISSION_STATUS)
    @PutMapping("/{permissionId}/status")
    @Operation(summary = "启用或禁用权限")
    public Result<Void> changeStatus(@PathVariable("permissionId") Long permissionId, @RequestBody StatusDTO statusDTO) {
        permissionService.changeStatus(permissionId, statusDTO.getStatus());
        return Result.success();
    }

    @Audit(permission = PERMISSION_VIEW)
    @GetMapping("/export")
    @Operation(summary = "权限数据导出")
    public void export(PermissionPageQueryDTO dto, HttpServletResponse response) {

        List<PermissionExcelVO> data = permissionService.listForExport(dto);

        try {
            response.setContentType(
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");

            EasyExcelFactory.write(response.getOutputStream(), PermissionExcelVO.class)
                            .registerWriteHandler(ExcelStyleUtil.defaultStyle())
                            .sheet("权限数据")
                            .doWrite(data);

        } catch (IOException e) {
            throw new RuntimeException("导出 Excel 失败", e);
        }
    }
}
