package com.ymjrhk.rbac.controller;

import com.alibaba.fastjson2.JSON;
import com.ymjrhk.rbac.constant.SuccessConstant;
import com.ymjrhk.rbac.dto.UserLoginDTO;
import com.ymjrhk.rbac.result.Result;
import com.ymjrhk.rbac.service.AuditLogService;
import com.ymjrhk.rbac.service.AuthService;
import com.ymjrhk.rbac.vo.UserLoginVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.ymjrhk.rbac.utils.IpUtil.getClientIp;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "认证模块")
public class AuthController {

    private final AuthService authService;

    private final AuditLogService auditLogService;

    /**
     * 用户登录
     *
     * @param userLoginDTO
     * @return
     */
    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "用户名密码登录")
    public Result<UserLoginVO> login(@RequestBody @Valid UserLoginDTO userLoginDTO,
                                     HttpServletRequest request) {
        log.info("用户登录：{}", userLoginDTO);

        try {
            UserLoginVO userLoginVO = authService.login(userLoginDTO);

            log.info("登录成功，将保存到审计日志表中...");

            // 登录成功日志
            auditLogService.saveLoginLog(
                    userLoginVO.getUserId(), // 登录成功，记录userId
                    userLoginDTO.getUsername(),
                    null, // 登录成功，不记录用户所填密码
                    getClientIp(request),
                    SuccessConstant.SUCCESS,
                    null
            );

            return Result.success(userLoginVO);

        } catch (Exception e) {
            log.warn("登录失败，将保存到审计日志表中...");

            // 登录失败日志
            auditLogService.saveLoginLog(
                    null, // 登录失败，如果用户名存在，则记录userId；否则不记录
                    userLoginDTO.getUsername(),
                    JSON.toJSONString(userLoginDTO),
                    getClientIp(request),
                    SuccessConstant.FAIL,
                    e.getMessage()
            );

            throw e;
        }
    }

    /**
     * 用户登出
     *
     * @param request
     * @return
     */
    @PostMapping("/logout")
    @Operation(summary = "用户登出", description = "用户登出")
    public void logout(HttpServletRequest request) {
        authService.logout();
        // TODO：写审计表
    }
}
