package com.learning.system.model.dto;

import com.learning.web.model.dto.BaseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(name = "角色权限关联", title = "角色权限关联")
public class RolePermissionDto extends BaseDto implements Serializable {

    /**
     * 角色id
     */
    @Schema(name = "角色id", title = "角色id", required = true)
    @NotEmpty(message = "角色id不能为空")
    private Long roleId;

    /**
     * 权限列表
     */
    @Schema(name = "权限列表", title = "权限列表")
    List<Long> permissionIdList;
}
