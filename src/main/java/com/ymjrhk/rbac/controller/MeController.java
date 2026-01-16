package com.ymjrhk.rbac.controller;

import com.ymjrhk.rbac.annotation.Audit;
import com.ymjrhk.rbac.dto.MePasswordUpdateDTO;
import com.ymjrhk.rbac.dto.MeUpdateDTO;
import com.ymjrhk.rbac.result.Result;
import com.ymjrhk.rbac.service.MeService;
import com.ymjrhk.rbac.vo.MeViewVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import static com.ymjrhk.rbac.constant.PermissionNameConstant.*;

@RestController
@RequestMapping("/api/me")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "个人模块")
public class MeController {
    private final MeService meService;

    /**
     * 查询个人信息
     *
     * @return
     */
    @Audit(permission = ME_VIEW)
    @GetMapping
    @Operation(summary = "查询个人信息")
    public Result<MeViewVO> query() {
        MeViewVO meViewVO = meService.query();
        return Result.success(meViewVO);
    }

    /**
     * 修改个人信息
     *
     * @param meUpdateDTO
     * @return
     */
    @Audit(permission = ME_UPDATE)
    @PutMapping
    @Operation(summary = "修改个人信息")
    public Result<Void> update(@RequestBody MeUpdateDTO meUpdateDTO) {
        meService.update(meUpdateDTO);
        return Result.success();
    }

    /**
     * 修改个人密码
     *
     * @param mePasswordUpdateDTO
     * @return
     */
    @Audit(permission = ME_PASSWORD_CHANGE)
    @PutMapping("/password/change")
    @Operation(summary = "修改个人密码")
    public Result<Void> changePassword(@RequestBody @Valid MePasswordUpdateDTO mePasswordUpdateDTO) {
        meService.changePassword(mePasswordUpdateDTO);
        return Result.success();
    }
}
