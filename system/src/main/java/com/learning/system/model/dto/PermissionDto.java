package com.learning.system.model.dto;

import com.learning.web.model.dto.BaseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(name = "权限 DTO", title = "权限 DTO")
public class PermissionDto extends BaseDto implements Serializable {

    private Long id;

    /**
     * 父权限id
     */
    @Schema(name = "父权限id", title = "父权限id", required = true)
    @NotNull(message = "父权限id不能为空")
    private Long pid;

    /**
     * 权限名
     */
    @Schema(name = "权限名", title = "权限名", required = true)
    @NotEmpty(message = "权限名不能为空")
    private String name;

    /**
     * 菜单权限url
     */
    @Schema(name = "菜单权限url", title = "菜单权限url")
    private String url;

    /**
     * 后台权限验证值
     */
    @Schema(name = "后台权限验证值", title = "后台权限验证值", required = true)
    @NotEmpty(message = "后台权限验证值不能为空")
    private String permission;

    /**
     * 权限类型 0：菜单 1：导航按钮 2:普通按钮
     */
    @Schema(name = "权限类型", title = "权限类型", required = true)
    @NotNull(message = "权限类型不能为空")
    private Integer permissionsType;

    /**
     * 菜单图标
     */
    @Schema(name = "菜单图标", title = "菜单图标")
    private String icon;

    /**
     * 菜单排序
     */
    @Schema(name = "菜单排序", title = "菜单排序")
    private Integer sort;

    /**
     * 是否启用
     */
    @Schema(name = "是否启用", title = "是否启用", required = true)
    @NotNull(message = "是否启用不能为空")
    private Integer enabled;

    /**
     * 子权限
     */
    @Schema(name = "子权限", title = "子权限")
    private List<PermissionDto> children;
}
