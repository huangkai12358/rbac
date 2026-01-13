package com.ymjrhk.rbac.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "审计日志查询返回参数")
/* 不需要userId和permissionId，是给人看的 */
public class AuditLogVO {
    private Long logSeq;

    private String username;

    private String permissionName;

    private String path;

    private String method;

    private String requestBody;

    private String ip;

    private Integer success;

    private String errorMessage;

    //    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss") // 已经扩展了全局消息转换器，所以不需要这个注解了
    private LocalDateTime createTime;
}
