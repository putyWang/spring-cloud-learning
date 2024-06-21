package com.learning.validation.core.vaildHandle;

import com.learning.core.utils.StringUtil;
import com.learning.validation.core.annotation.YHClassCheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author WangWei
 * @version v 1.0
 * @description
 * @date 2024-06-21
 **/
public class YHClassCheckHandle implements ConstraintValidator<YHClassCheck, Object> {
    private static final Logger log = LoggerFactory.getLogger(YHClassCheckHandle.class);
    private Class<? extends IClassCheck> checkClass;
    private String checkPty;

    public YHClassCheckHandle() {
    }

    public void initialize(YHClassCheck yhClassCheck) {
        this.checkClass = yhClassCheck.checkClass();
        this.checkPty = yhClassCheck.checkPty();
    }

    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (this.checkClass == null) {
            return false;
        } else {
            try {
                IClassCheck classCheck = (IClassCheck)this.checkClass.newInstance();
                CheckResult rs = classCheck.check(value);
                if (rs == null) {
                    return false;
                } else {
                    if (!rs.isValited()) {
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

                    return rs.isValited();
                }
            } catch (Exception var6) {
                log.error("类验证器,创建验证类失败{}", var6);
                context.buildConstraintViolationWithTemplate("类验证器,创建验证类失败!").addConstraintViolation();
                return false;
            }
        }
    }
}

