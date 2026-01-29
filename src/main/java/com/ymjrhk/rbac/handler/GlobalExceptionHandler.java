package com.ymjrhk.rbac.handler;

import com.ymjrhk.rbac.constant.MessageConstant;
import com.ymjrhk.rbac.exception.AccessDeniedException;
import com.ymjrhk.rbac.exception.BaseException;
import com.ymjrhk.rbac.exception.UserNotLoginException;
import com.ymjrhk.rbac.result.Result;
import com.ymjrhk.rbac.result.ResultCode;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.List;

import static com.ymjrhk.rbac.constant.MessageConstant.*;

/**
 * 全局异常处理器，处理项目中抛出的业务异常
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    private final View error;

    public GlobalExceptionHandler(View error) {
        this.error = error;
    }

    /**
     * 未登录
     *
     * @param ex
     * @return
     */
    @ExceptionHandler(UserNotLoginException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Result<Void> handleUserNotLogin(UserNotLoginException ex) {
        log.warn("{}", ex.getMessage());
        return Result.error(ResultCode.UNAUTHORIZED.getCode(), ex.getMessage());
    }

    /**
     * 无权限访问
     *
     * @param ex
     * @return
     */
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Result<Void> handleAccessDenied(AccessDeniedException ex) {
        log.warn("{}", ex.getMessage());
        return Result.error(ResultCode.FORBIDDEN.getCode(), ex.getMessage());
    }

    /**
     * 捕获业务异常
     *
     * @param ex
     * @return
     */
    @ExceptionHandler(BaseException.class)
    public Result<Void> handleBusiness(BaseException ex) {
        log.warn("业务异常：{}", ex.getMessage());
        return Result.error(ex.getMessage());
    }

    /**
     * 捕获数据库异常
     *
     * @param ex
     * @return
     */
    @ExceptionHandler(DuplicateKeyException.class)
    public Result<Void> handleSql(DuplicateKeyException ex) {
        log.warn("唯一键冲突：{}", ex.getMessage());

        String message = ex.getMostSpecificCause().getMessage();
        if (message != null && message.contains("sys_user.username")) {
            return Result.error("用户名称已存在");
        }
        if (message != null && message.contains("sys_role.role_name")) {
            return Result.error("角色名称已存在");
        }
        if (message != null && message.contains("sys_permission.permission_name")) {
            return Result.error("权限名称已存在");
        }
        return Result.error(MessageConstant.ALREADY_EXISTED);
    }

    /**
     * 请求参数格式错误
     *
     * @param ex
     * @return
     */
    @ExceptionHandler(HttpMessageNotReadableException.class) // 请求体无法反序列化成 Java 对象，JSON 没读成功，@Valid 校验还没开始
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public Result<Void> handleBadRequest(HttpMessageNotReadableException ex,
                                         HttpServletRequest request) {
        log.warn("{}：{}", PARAMETER_FORMAT_ERROR, ex.getMessage());

        request.setAttribute(ERROR_MESSAGE, PARAMETER_FORMAT_ERROR); // 在 ExceptionHandler 里显式“埋点”，用于在同一次请求中共享数据。生命周期：从请求进入容器 → 响应返回客户端

        return Result.error(PARAMETER_FORMAT_ERROR);
    }

    /**
     * 字段检验
     *
     * @param ex
     * @return
     */
    @ExceptionHandler(MethodArgumentNotValidException.class) // 请求体已经成功反序列化成 Java 对象，但字段校验 @Valid 失败
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public Result<Void> handleValidationException(MethodArgumentNotValidException ex,
                                                  HttpServletRequest request) {
        // 拿到所有字段错误
        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();

        // 一般做法：取第一个错误
        FieldError firstError = fieldErrors.getFirst();

        log.warn("{} —— {}：{}", FIELD_VALID_FAILED, firstError.getDefaultMessage(), ex.getMessage());

        request.setAttribute(ERROR_MESSAGE, firstError.getDefaultMessage());

        return Result.error(firstError.getDefaultMessage());
    }

    /**
     * 静态资源不存在，忽略即可
     *
     * @param ex
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public void handleNoResource(NoResourceFoundException ex) {
        log.warn("静态资源不存在，忽略：{}", ex.getMessage());
        // favicon.ico / 静态资源不存在，忽略即可
    }

    /**
     * 兜底处理
     *
     * @param ex
     * @return
     */
    @ExceptionHandler(Exception.class)
    public Result<Void> handleAll(Exception ex) {
        log.warn("系统异常：{}", ex.getMessage());
        log.warn("打印堆栈：");
        ex.printStackTrace();
        return Result.error("系统繁忙，请稍后再试");
    }
}
