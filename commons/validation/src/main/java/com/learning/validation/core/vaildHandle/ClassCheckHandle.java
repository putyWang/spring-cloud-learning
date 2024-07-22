package com.learning.validation.core.vaildHandle;

import com.learning.core.utils.StringUtil;
import com.learning.validation.core.annotation.ClassCheck;
import com.learning.validation.core.classcheck.CheckResult;
import com.learning.validation.core.classcheck.IClassCheck;
import lombok.extern.log4j.Log4j2;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author WangWei
 * @version v 1.0
 * @description 类验证（ClassCheck）处理器
 * @date 2024-06-21
 **/
@Log4j2
public class ClassCheckHandle implements ConstraintValidator<ClassCheck, Object> {

    /**
     * 验证类
     */
    private Class<? extends IClassCheck> checkClass;

    /**
     * 节点字段名
     */
    private String checkPty;

    @Override
    public void initialize(ClassCheck classCheck) {
        this.checkClass = classCheck.checkClass();
        this.checkPty = classCheck.checkPty();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (this.checkClass == null) {
            return false;
        }

        try {
            // 1 调用自定义验证器对数据进行验证
            CheckResult rs = this.checkClass.newInstance().check(value);
            if (rs == null) {
                return false;
            }
            // 2 构建自定义失败校验结果
            if (! rs.isValidate()) {
                // 2.1 禁用默认验证
                context.disableDefaultConstraintViolation();
                // 2.2 设置验证失败信息
                String failPty = StringUtil.isNotBlank(rs.getFailPty()) ? rs.getFailPty() : this.checkPty;
                if (StringUtil.isNotBlank(failPty)) {
                    context.buildConstraintViolationWithTemplate(rs.getMsg())
                            .addPropertyNode(failPty)
                            .addConstraintViolation();
                } else {
                    context.buildConstraintViolationWithTemplate(rs.getMsg())
                            .addConstraintViolation();
                }
            }
            // 3 返回是否验证成功
            return rs.isValidate();
        } catch (Exception e) {
            log.error("类验证器,创建验证类失败", e);
            context.buildConstraintViolationWithTemplate("类验证器,创建验证类失败!")
                    .addConstraintViolation();
            return false;
        }
    }
}

