package com.ymjrhk.rbac.handler;

import com.ymjrhk.rbac.constant.MessageConstant;
import com.ymjrhk.rbac.exception.BaseException;
import com.ymjrhk.rbac.exception.UserNotLoginException;
import com.ymjrhk.rbac.result.Result;
import com.ymjrhk.rbac.result.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;

/**
 * 全局异常处理器，处理项目中抛出的业务异常
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    /**
     * 未登录
     * @param ex
     * @return
     */
    @ExceptionHandler(UserNotLoginException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Result<Void> handleUserNotLogin(UserNotLoginException ex) {
        return Result.error(ResultCode.UNAUTHORIZED.getCode(), ex.getMessage());
    }

    /**
     * 捕获业务异常
     * @param ex
     * @return
     */
    @ExceptionHandler(BaseException.class)
    public Result<Void> handleBusiness(BaseException ex){
        log.error("业务异常：{}", ex.getMessage());
        return Result.error(ex.getMessage());
    }

    /**
     * 捕获数据库异常
     * @param ex
     * @return
     */
    @ExceptionHandler(DuplicateKeyException.class)
    public Result<Void> handleSql(DuplicateKeyException ex) {
        log.warn("唯一键冲突", ex);

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
     * @param ex
     * @return
     */
    @ExceptionHandler(HttpMessageNotReadableException.class) // 请求体无法反序列化成 Java 对象，JSON 没读成功，@Valid 校验还没开始
    public Result<Void> handleBadRequest(HttpMessageNotReadableException ex) {
        return Result.error("请求参数格式错误");
    }


    /**
     * 字段检验
     * @param ex
     * @return
     */
    @ExceptionHandler(MethodArgumentNotValidException.class) // 请求体已经成功反序列化成 Java 对象，但字段校验失败
    public Result<Void> handleValidationException(MethodArgumentNotValidException ex) {

        // 拿到所有字段错误
        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();

        // 一般做法：取第一个错误
        FieldError error = fieldErrors.getFirst();

        return Result.error(error.getDefaultMessage());
    }

    /**
     * 兜底处理
     * @param ex
     * @return
     */
    @ExceptionHandler(Exception.class)
    public Result<Void> handleAll(Exception ex) {
        log.error("系统异常", ex);
        return Result.error("系统繁忙，请稍后再试");
    }
}
