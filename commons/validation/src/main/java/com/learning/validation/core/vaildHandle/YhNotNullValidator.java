package com.learning.validation.core.vaildHandle;

import com.learning.validation.core.annotation.IdCardValid;
import com.learning.validation.core.annotation.PhoneNumberValid;
import com.learning.validation.core.annotation.UrlValid;
import com.learning.validation.core.annotation.YhNotNullValid;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.internal.constraintvalidators.bv.EmailValidator;
import org.hibernate.validator.internal.constraintvalidators.bv.PatternValidator;
import org.hibernate.validator.internal.constraintvalidators.hv.LengthValidator;
import org.hibernate.validator.internal.engine.constraintvalidation.ConstraintValidatorDescriptor;
import org.hibernate.validator.internal.metadata.core.ConstraintHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.constraints.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * @author WangWei
 * @version v 1.0
 * @description
 * @date 2024-06-21
 **/
public class YhNotNullValidator implements ConstraintValidator<YhNotNullValid, Object> {
    private static final Logger log = LoggerFactory.getLogger(YhNotNullValidator.class);
    Size[] size;
    Max[] max;
    Min[] min;
    Email[] email;
    Pattern[] pattern;
    EnumValue[] enumValue;
    IdCardValid[] idCardValid;
    PhoneNumberValid[] phoneNumberValid;
    UrlValid[] urlValid;
    Length[] length;
    private static HashMap<Integer, ConstraintValidator<Annotation, Object>> constraintValidatorMap = new HashMap();
    private static final ConstraintHelper constraintHelper = new ConstraintHelper();

    public YhNotNullValidator() {
    }

    public void initialize(YhNotNullValid yhMutiValid) {
        this.size = yhMutiValid.size();
        this.max = yhMutiValid.max();
        this.min = yhMutiValid.min();
        this.email = yhMutiValid.email();
        this.pattern = yhMutiValid.pattern();
        this.enumValue = yhMutiValid.enumValue();
        this.idCardValid = yhMutiValid.idCardValid();
        this.phoneNumberValid = yhMutiValid.phoneNumberValid();
        this.urlValid = yhMutiValid.urlValid();
        this.length = yhMutiValid.length();
    }

    public boolean isValid(Object value, ConstraintValidatorContext context) {
        try {
            if (null == value) {
                return true;
            } else if (value instanceof String && value + "" == "") {
                return true;
            } else {
                String message = "";
                boolean enumValided = true;
                boolean idCardValided = true;
                boolean lengthValided = true;
                boolean phoneNumberValided = true;
                boolean urlValided = true;
                boolean patternValided = true;
                boolean sizeValided = true;
                boolean maxValided = true;
                boolean minValided = true;
                boolean emailValided = true;
                if (!ObjectUtils.isEmpty(this.enumValue)) {
                    message = message + this.enumValue[0].message() + " , ";
                    EnumValueValidator enumValueValidator = new EnumValueValidator();
                    enumValueValidator.initialize(this.enumValue[0]);
                    enumValided = enumValueValidator.isValid(value, context);
                }

                if (!ObjectUtils.isEmpty(this.idCardValid)) {
                    message = message + this.idCardValid[0].message() + " , ";
                    idCardValided = (new IdCardValidHandle()).isValid(value, context);
                }

                if (!ObjectUtils.isEmpty(this.length)) {
                    LengthValidator lengthValidator = new LengthValidator();
                    lengthValidator.initialize(this.length[0]);
                    lengthValided = lengthValidator.isValid(value + "", context);
                    message = message + "长度需要在[" + this.length[0].min() + "]和[" + this.length[0].max() + "]之间 , ";
                }

                if (!ObjectUtils.isEmpty(this.phoneNumberValid)) {
                    message = message + this.phoneNumberValid[0].message() + " , ";
                    phoneNumberValided = (new PhoneNumberValidHandle()).isValid(value + "", context);
                }

                if (!ObjectUtils.isEmpty(this.urlValid)) {
                    message = message + this.urlValid[0].message() + " , ";
                    urlValided = (new UrlValidHandle()).isValid(value + "", context);
                }

                if (!ObjectUtils.isEmpty(this.pattern)) {
                    PatternValidator patternValidator = new PatternValidator();
                    patternValidator.initialize(this.pattern[0]);
                    patternValided = patternValidator.isValid(value + "", context);
                    message = message + this.pattern[0].message() + " , ";
                }

                if (!ObjectUtils.isEmpty(this.email)) {
                    EmailValidator emailValidator = new EmailValidator();
                    emailValidator.initialize(this.email[0]);
                    emailValided = emailValidator.isValid(value + "", context);
                    message = message + this.email[0].message() + " , ";
                }

                if (!ObjectUtils.isEmpty(this.size)) {
                    String message1 = this.size[0].message();
                    sizeValided = this.primitiveValidation(value, Size.class, context, this.size[0]);
                    if (StringUtils.isEmpty(message1)) {
                        message = message + "个数必须在[" + this.size[0].min() + "]和[" + this.size[0].max() + "]之间 , ";
                    } else {
                        message = message + message1;
                    }
                }

                if (!ObjectUtils.isEmpty(this.max)) {
                    maxValided = this.primitiveValidation(value, Max.class, context, this.max[0]);
                    message = message + "最大不能超过[" + this.max[0].value() + "] , ";
                }

                if (!ObjectUtils.isEmpty(this.min)) {
                    minValided = this.primitiveValidation(value, Min.class, context, this.min[0]);
                    message = message + "最小不能小于[" + this.min[0].value() + "] , ";
                }

                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(value + " , " + message).addConstraintViolation();
                return enumValided && idCardValided && lengthValided && phoneNumberValided && urlValided && patternValided && sizeValided && maxValided && minValided && emailValided;
            }
        } catch (Throwable var15) {
            throw var15;
        }
    }

    private boolean primitiveValidation(Object value, Class clazz, ConstraintValidatorContext context, Annotation annotation) throws IllegalAccessException, InstantiationException, ClassNotFoundException {
        List<ConstraintValidatorDescriptor<?>> validatorDescriptors = constraintHelper.getAllValidatorDescriptors(clazz);
        Iterator var6 = validatorDescriptors.iterator();

        ConstraintValidatorDescriptor validatorDescriptor;
        String typeClassName;
        String validatorClassName;
        boolean instance;
        do {
            if (!var6.hasNext()) {
                return true;
            }

            validatorDescriptor = (ConstraintValidatorDescriptor)var6.next();
            Class<?> valueClass = value.getClass();
            typeClassName = valueClass.getTypeName();
            Type validatedType = validatorDescriptor.getValidatedType();
            validatorClassName = validatedType.getTypeName();
            instance = false;
            if (!validatorClassName.endsWith("[]")) {
                instance = Class.forName(validatorClassName).isInstance(value);
            }

            log.debug("valueType : " + typeClassName + " , validatorType: " + validatorClassName + " , instanceof: " + instance);
        } while(!typeClassName.equals(validatorClassName) && !instance);

        int hashCode = validatorDescriptor.hashCode();
        ConstraintValidator<Annotation, Object> constraintValidator = null;
        if (constraintValidatorMap.get(hashCode) != null) {
            constraintValidator = (ConstraintValidator)constraintValidatorMap.get(hashCode);
        } else {
            constraintValidator = (ConstraintValidator)validatorDescriptor.getValidatorClass().newInstance();
            constraintValidatorMap.put(hashCode, constraintValidator);
        }

        constraintValidator.initialize(annotation);
        return constraintValidator.isValid(value, context);
    }
}

