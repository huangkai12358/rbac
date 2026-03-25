package com.ymjrhk.rbac.result;

import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;
import java.util.Objects;

@Schema(description = "返回结果封装")
public class Result<T> implements Serializable {

    @Schema(description = "业务状态码", example = "0 成功，1 和其它数字为失败")
    private Integer code; // 编码：0成功，1和其它数字为失败
    @Schema(description = "错误提示信息")
    private String errorMessage; // 错误信息
    @Schema(description = "返回数据")
    private T data; // 数据

    /* ===== 成功 ===== */
    public static <T> Result<T> success() {
        return success(null);
    }

    public static <T> Result<T> success(T data) {
        return of(0, null, data);
    }

    /* ===== 失败 ===== */
    public static <T> Result<T> error(Integer code, String errorMessage) {
        return of(code, errorMessage, null);
    }

    public static <T> Result<T> error(String errorMessage) {
        return error(1, errorMessage);
    }

    /* ===== 通用 ===== */
    public static <T> Result<T> of(Integer code, String errorMessage, T data) {
        Result<T> r = new Result<>();
        r.code = code;
        r.errorMessage = errorMessage;
        r.data = data;
        return r;
    }
    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Result that = (Result) o;
        return Objects.equals(code, that.code) && Objects.equals(errorMessage, that.errorMessage) && Objects.equals(data, that.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, errorMessage, data);
    }

    @Override
    public String toString() {
        return "Result" + "{" + "code=" + code + ", " + "errorMessage=" + errorMessage + ", " + "data=" + data + "}";
    }

}