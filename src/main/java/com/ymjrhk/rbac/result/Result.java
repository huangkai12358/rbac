package com.ymjrhk.rbac.result;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "返回结果封装")
public class Result<T> implements Serializable {

    @Schema(description = "响应码", example = "0成功，1和其它数字为失败")
    private Integer code; //编码：0成功，1和其它数字为失败
    @Schema(description = "错误提示信息")
    private String errorMessage; //错误信息
    @Schema(description = "数据")
    private T data; //数据

    public static <T> Result<T> success() {
        Result<T> result = new Result<>();
        result.code = 0;
        return result;
    }

    public static <T> Result<T> success(T object) {
        Result<T> result = new Result<>();
        result.data = object;
        result.code = 0;
        return result;
    }

    public static <T> Result<T> error(String msg) {
        Result<T> result = new Result<>();
        result.errorMessage = msg;
        result.code = 1;
        return result;
    }

}
