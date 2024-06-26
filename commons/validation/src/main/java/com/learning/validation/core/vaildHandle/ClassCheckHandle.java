package com.learning.validation.core.vaildHandle;

import com.learning.core.utils.StringUtil;
import com.learning.validation.core.annotation.ClassCheck;
import com.learning.validation.core.yhclasscheck.CheckResult;
import com.learning.validation.core.yhclasscheck.IClassCheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author WangWei
 * @version v 1.0
 * @description 类验证（ClassCheck）处理器
 * @date 2024-06-21
 **/
public class ClassCheckHandle implements ConstraintValidator<ClassCheck, Object> {
    private static final Logger log = LoggerFactory.getLogger(ClassCheckHandle.class);
    private Class<? extends IClassCheck<Object>> checkClass;
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
            CheckResult rs = this.checkClass.newInstance().check(value);
            if (rs == null) {
                return false;
            }

            if (!rs.isValidate()) {
                String failPty = null;
                if (StringUtil.isNotBlank(rs.getFailPty())) {
                    failPty = rs.getFailPty();
                } else if (StringUtil.isNotBlank(this.checkPty)) {
                    failPty = this.checkPty;
                }

                context.disableDefaultConstraintViolation();
                if (StringUtil.isNotBlank(failPty)) {
                    context.buildConstraintViolationWithTemplate(rs.getMsg()).addPropertyNode(failPty).addConstraintViolation();
                } else {
                    context.buildConstraintViolationWithTemplate(rs.getMsg()).addConstraintViolation();
                }
            }

            return rs.isValidate();
        } catch (Exception var6) {
            log.error("类验证器,创建验证类失败", var6);
            context.buildConstraintViolationWithTemplate("类验证器,创建验证类失败!").addConstraintViolation();
            return false;
        }
    }
}

