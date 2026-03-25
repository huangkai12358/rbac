package com.ymjrhk.rbac.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Objects;

@Schema(description = "id 列表参数")
public class IdsDTO {
    @Schema(description = "id 列表", example = "[1,2,3]")
    @NotNull(message = "id 列表不能为 null")
    // 列表可以为空列表，表示清空角色或权限
    private List<Long> ids;
    public List<Long> getIds() {
        return ids;
    }

    public void setIds(List<Long> ids) {
        this.ids = ids;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        IdsDTO that = (IdsDTO) o;
        return Objects.equals(ids, that.ids);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ids);
    }

    @Override
    public String toString() {
        return "IdsDTO" + "{" + "ids=" + ids + "}";
    }

}
