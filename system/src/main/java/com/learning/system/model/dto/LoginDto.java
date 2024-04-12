package com.learning.system.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "登陆 dto")
public class LoginDto {

    @Schema(name = "用户名", title = "用户名")
    private String username;

    @Schema(name = "密码", title = "密码")
    private String password;
}
