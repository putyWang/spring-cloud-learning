package com.learning.validation.core.function;

/**
 * @author WangWei
 * @version v 1.0
 * @description 枚举验证继承接口
 * @date 2024-06-27
 **/
public interface EnumValueValidFunction {

    /**
     * 是否包含指定 code
     *
     * @param code 类型
     * @return 匹配枚举
     */
    boolean containsCode(String code);
}
