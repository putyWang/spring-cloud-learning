package com.learning.system.model.dto;

import com.learning.web.model.dto.BaseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(name = "角色 DTO", title = "角色 DTO")
public class RoleDto extends BaseDto implements Serializable {

    private Long id;

    /**
     * 角色名称
     */
    @Schema(name = "角色名称", title = "角色名称", required = true)
    @NotEmpty(message = "角色名称不能为空")
    private String roleName;

    /**
     * 角色编码
     */
    @Schema(name = "角色编码", title = "角色编码", required = true)
    @NotEmpty(message = "角色编码不能为空")
    private String roleCode;

    @Schema(name = "创建时间", title = "创建时间")
    private Date createTime;

    /**
     * 职能级别，管理员为1，用户为2
     */
    @Schema(name = "职能级别，管理员为1，用户为2", title = "职能级别，管理员为1，用户为2", required = true)
    @NotNull(message = "职能级别不能为空")
    private Integer roleLevel;

    /**
     * 角色首页路由
     */
    @Schema(name = "角色首页路由", title = "角色首页路由")
    private String indexUrl;

    /**
     * 角色描述
     */
    @Schema(name = "角色描述", title = "角色描述")
    private String roleDesc;

    /**
     * 创建用户id
     */
    @Schema(name = "创建用户id", title = "创建用户id")
    private Long createdUserId;

    @Schema(name = "创建用户", title = "创建用户")
    private String createdUser;
}
