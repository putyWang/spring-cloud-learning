package com.learning.validation.core.vaildHandle;

import com.learning.validation.core.annotation.*;
import lombok.extern.log4j.Log4j2;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.internal.constraintvalidators.bv.EmailValidator;
import org.hibernate.validator.internal.constraintvalidators.bv.PatternValidator;
import org.hibernate.validator.internal.constraintvalidators.hv.LengthValidator;
import org.hibernate.validator.internal.engine.constraintvalidation.ConstraintValidatorDescriptor;
import org.hibernate.validator.internal.metadata.core.ConstraintHelper;
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
@Log4j2
public class NotNullValidator implements ConstraintValidator<NotNullValid, Object> {
    Size size;
    Max[] max;
    Min[] min;
    Email[] email;
    Pattern[] pattern;
    EnumValue[] enumValue;
    IdCardValid[] idCardValid;
    PhoneNumberValid[] phoneNumberValid;
    UrlValid[] urlValid;
    Length[] length;

    private static final HashMap<Integer, ConstraintValidator<Annotation, ?>> CONSTRAINT_VALIDATOR_MAP = new HashMap<>();

    private static final ConstraintHelper constraintHelper = ConstraintHelper.forAllBuiltinConstraints();

    @Override
    public void initialize(NotNullValid multiValid) {
        this.size = multiValid.size();
        this.max = multiValid.max();
        this.min = multiValid.min();
        this.email = multiValid.email();
        this.pattern = multiValid.pattern();
        this.enumValue = multiValid.enumValue();
        this.idCardValid = multiValid.idCardValid();
        this.phoneNumberValid = multiValid.phoneNumberValid();
        this.urlValid = multiValid.urlValid();
        this.length = multiValid.length();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        try {
            if (null == value) {
                return true;
            } else if (value instanceof String && "".equals(value + "")) {
                return true;
            } else {
                String message = "";
                boolean enumValidated = true;
                boolean idCardValidated = true;
                boolean lengthValidated = true;
                boolean phoneNumberValidated = true;
                boolean urlValidated = true;
                boolean patternValidated = true;
                boolean sizeValidated = true;
                boolean maxValidated = true;
                boolean minValidated = true;
                boolean emailValidated = true;
                if (!ObjectUtils.isEmpty(this.enumValue)) {
                    message = message + this.enumValue[0].message() + " , ";
                    EnumValueValidator enumValueValidator = new EnumValueValidator();
                    enumValueValidator.initialize(this.enumValue[0]);
                    enumValidated = enumValueValidator.isValid(value, context);
                }

                if (!ObjectUtils.isEmpty(this.idCardValid)) {
                    message = message + this.idCardValid[0].message() + " , ";
                    idCardValidated = (new IdCardValidHandle()).isValid(value, context);
                }

                if (!ObjectUtils.isEmpty(this.length)) {
                    LengthValidator lengthValidator = new LengthValidator();
                    lengthValidator.initialize(this.length[0]);
                    lengthValidated = lengthValidator.isValid(value + "", context);
                    message = message + "长度需要在[" + this.length[0].min() + "]和[" + this.length[0].max() + "]之间 , ";
                }

                if (!ObjectUtils.isEmpty(this.phoneNumberValid)) {
                    message = message + this.phoneNumberValid[0].message() + " , ";
                    phoneNumberValidated = (new PhoneNumberValidHandle()).isValid(value + "", context);
                }

                if (!ObjectUtils.isEmpty(this.urlValid)) {
                    message = message + this.urlValid[0].message() + " , ";
                    urlValidated = (new UrlValidHandle()).isValid(value + "", context);
                }

                if (!ObjectUtils.isEmpty(this.pattern)) {
                    PatternValidator patternValidator = new PatternValidator();
                    patternValidator.initialize(this.pattern[0]);
                    patternValidated = patternValidator.isValid(value + "", context);
                    message = message + this.pattern[0].message() + " , ";
                }

                if (!ObjectUtils.isEmpty(this.email)) {
                    EmailValidator emailValidator = new EmailValidator();
                    emailValidator.initialize(this.email[0]);
                    emailValidated = emailValidator.isValid(value + "", context);
                    message = message + this.email[0].message() + " , ";
                }

                if (!ObjectUtils.isEmpty(this.size)) {
                    String message1 = this.size[0].message();
                    sizeValidated = this.primitiveValidation(value, Size.class, context, this.size[0]);
                    if (StringUtils.isEmpty(message1)) {
                        message = message + "个数必须在[" + this.size[0].min() + "]和[" + this.size[0].max() + "]之间 , ";
                    } else {
                        message = message + message1;
                    }
                }

                if (!ObjectUtils.isEmpty(this.max)) {
                    maxValidated = this.primitiveValidation(value, Max.class, context, this.max[0]);
                    message = message + "最大不能超过[" + this.max[0].value() + "] , ";
                }

                if (!ObjectUtils.isEmpty(this.min)) {
                    minValidated = this.primitiveValidation(value, Min.class, context, this.min[0]);
                    message = message + "最小不能小于[" + this.min[0].value() + "] , ";
                }

                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(value + " , " + message).addConstraintViolation();
                return enumValidated && idCardValidated && lengthValidated && phoneNumberValidated && urlValidated && patternValidated && sizeValidated && maxValidated && minValidated && emailValidated;
            }
        } catch (Throwable var15) {
            throw var15;
        }
    }

    private boolean primitiveValidation(Object value, Class<Annotation> clazz, ConstraintValidatorContext context, Annotation annotation)
            throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        for (ConstraintValidatorDescriptor<Annotation> validatorDescriptor : constraintHelper.getAllValidatorDescriptors(clazz)) {
            String validatorClassName = validatorDescriptor.getValidatedType().getTypeName();
            if (! value.getClass().getTypeName().equals(validatorClassName)) {
                continue;
            }

            if (validatorClassName.endsWith("[]") || ! Class.forName(validatorClassName).isInstance(value)) {
                break;
            }
            ConstraintValidator<Annotation, ?> constraintValidator = CONSTRAINT_VALIDATOR_MAP
                    .putIfAbsent(validatorDescriptor.hashCode(), validatorDescriptor.getValidatorClass().newInstance());
            constraintValidator.initialize(annotation);
            return constraintValidator.isValid(value, context);
        }


    }
}

