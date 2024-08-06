package com.learning.system.model.dto;

import com.learning.core.domain.annotation.Unique;
import com.learning.web.model.dto.BaseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(name = "用户角色关联类", title = "用户角色关联类")
public class UserRoleDto extends BaseDto {

    private Long id;
    /**
     * 用户id
     */
    @Schema(name = "用户id", title = "用户id", required = true)
    @NotNull(message = "用户 id 不能为空")
    private Long userId;

    @Schema(name = "用户名", title = "用户名")
    private String userName;

    /**
     * 角色编码
     */
    @Schema(name = "角色id", title = "角色id", required = true)
    @NotNull(message = "角色 id 不能为空")
    private Long roleId;

    @Schema(name = "角色编码", title = "角色编码")
    private String roleCode;
}
