package com.learning.system.model.dto;

import com.learning.web.model.dto.BaseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(name = "用户信息返回类", title = "用户信息返回类")
public class UserDto extends BaseDto {

    private Long id;

    @Schema(name = "用户名字", title = "用户名字", required = true)
    @NotEmpty(message = "用户名不能为空")
    private String username;

    @Schema(name = "用户昵称", title = "用户昵称", required = true)
    @NotEmpty(message = "用户昵称不能为空")
    private String nickName;

    @Schema(name = "真实姓名", title = "真实姓名")
    private String realName;

    @Schema(name = "用户密码", title = "用户密码")
    private String password;

    @Schema(name = "性别", title = "性别")
    @NotNull(message = "性别不能为空")
    private Integer sex;

    @Schema(name = "电话号码", title = "电话号码", required = true)
    @NotNull(message = "电话号码不能为空")
    private String phone;

    /**
     * 邮箱地址
     * 正则表达式（"^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\.[a-zA-Z0-9_-]+)+$"）
     */
    @Schema(name = "邮箱地址", title = "邮箱地址", required = true)
    @NotEmpty(message = "邮箱地址不能为空")
    @Email(regexp = "^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$", message = "邮箱地址格式有误，请重新输入")
    private String email;

    @Schema(name = "用户状态", title = "默认为1")
    private Integer status;

    @Schema(name = "角色编码")
    private String roleCode;

    @Schema(name = "权限列表")
    private List<String> permission;
}
