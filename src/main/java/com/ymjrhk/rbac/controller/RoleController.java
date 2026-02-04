package com.ymjrhk.rbac.controller;

import com.alibaba.excel.EasyExcelFactory;
import com.ymjrhk.rbac.annotation.Audit;
import com.ymjrhk.rbac.dto.RoleCreateDTO;
import com.ymjrhk.rbac.dto.RoleDTO;
import com.ymjrhk.rbac.dto.RolePageQueryDTO;
import com.ymjrhk.rbac.dto.StatusDTO;
import com.ymjrhk.rbac.result.PageResult;
import com.ymjrhk.rbac.result.Result;
import com.ymjrhk.rbac.service.RoleService;
import com.ymjrhk.rbac.utils.ExcelStyleUtil;
import com.ymjrhk.rbac.vo.RoleExcelVO;
import com.ymjrhk.rbac.vo.RoleVO;
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
@RequestMapping("/api/roles")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "角色管理模块")
public class RoleController {
    private final RoleService roleService;

    /**
     * 创建角色
     *
     * @param roleCreateDTO
     * @return
     */
    @Audit(permission = ROLE_CREATE)
    @PostMapping
    @Operation(summary = "创建角色")
    public Result<Long> createRole(@RequestBody @Valid RoleCreateDTO roleCreateDTO) {
        log.info("创建角色：{}", roleCreateDTO);
        Long roleId = roleService.create(roleCreateDTO);
        return Result.success(roleId);
    }

    /**
     * 角色分页查询
     *
     * @param rolePageQueryDTO
     * @return
     */
    @Audit(permission = ROLE_VIEW)
    @GetMapping("/page")
    @Operation(summary = "角色分页查询")
    public Result<PageResult> pageQuery(RolePageQueryDTO rolePageQueryDTO) {
        log.info("角色分页查询，参数为：{}", rolePageQueryDTO);
        PageResult pageResult = roleService.pageQuery(rolePageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 根据 roleId 查询角色
     *
     * @param roleId
     * @return
     */
    @Audit(permission = ROLE_VIEW)
    @GetMapping("/{roleId}")
    @Operation(summary = "根据 roleId 查询角色")
    public Result<RoleVO> queryRoleById(@PathVariable("roleId") Long roleId) {
        RoleVO roleVO = roleService.getByRoleId(roleId);
        return Result.success(roleVO);
    }

    /**
     * 修改角色
     *
     * @param roleId
     * @param roleDTO
     * @return
     */
    @Audit(permission = ROLE_UPDATE)
    @PutMapping("/{roleId}")
    @Operation(summary = "修改角色")
    public Result<Void> updateRole(@PathVariable Long roleId, @RequestBody RoleDTO roleDTO) {
        log.info("修改角色：roleId: {}，roleDTO: {}", roleId, roleDTO);
        roleService.update(roleId, roleDTO);
        return Result.success();
    }

    /**
     * 启用或禁用角色
     *
     * @param roleId
     * @param statusDTO
     * @return
     */
    @Audit(permission = ROLE_STATUS)
    @PutMapping("/{roleId}/status")
    @Operation(summary = "启用或禁用角色")
    public Result<Void> changeStatus(@PathVariable("roleId") Long roleId, @RequestBody StatusDTO statusDTO) {
        roleService.changeStatus(roleId, statusDTO.getStatus());
        return Result.success();
    }

    @Audit(permission = ROLE_VIEW)
    @GetMapping("/export")
    @Operation(summary = "角色数据导出")
    public void export(RolePageQueryDTO dto, HttpServletResponse response) {

        List<RoleExcelVO> data = roleService.listForExport(dto);

        try {
            response.setContentType(
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");

            EasyExcelFactory.write(response.getOutputStream(), RoleExcelVO.class)
                            .registerWriteHandler(ExcelStyleUtil.defaultStyle())
                            .sheet("角色数据")
                            .doWrite(data);

        } catch (IOException e) {
            throw new RuntimeException("导出 Excel 失败", e);
        }
    }
}
