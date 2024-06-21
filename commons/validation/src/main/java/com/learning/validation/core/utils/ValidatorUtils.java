package com.learning.validation.core.utils;

import com.learning.core.utils.StringUtil;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author WangWei
 * @version v 1.0
 * @description
 * @date 2024-06-21
 **/
public class ValidatorUtils {
    private static Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    public ValidatorUtils() {
    }

    public static void validateEntity(Object object, Class<?>... groups) throws ServiceException {
        Set<ConstraintViolation<Object>> constraintViolations = validator.validate(object, groups);
        if (!constraintViolations.isEmpty()) {
            Map<String, Object> errors = new HashMap();
            Iterator var4 = constraintViolations.iterator();

            while(var4.hasNext()) {
                ConstraintViolation<Object> constraint = (ConstraintViolation)var4.next();
                errors.put(constraint.getPropertyPath().toString(), constraint.getMessage());
            }

            throw new ServiceException("数据校验错误", errors);
        }
    }

    public static void OrmAddValidate(Map<String, String> data, Map<String, Map<String, String>> files) {
        validateEntity(data, files, true);
    }

    public static void OrmUpdataValidate(Map<String, String> data, Map<String, Map<String, String>> files) {
        validateEntity(data, files, false);
    }

    private static void validateEntity(Map<String, String> data, Map<String, Map<String, String>> files, boolean isAdd) {
        Map<String, Object> errors = new HashMap();
        if (isAdd) {
            errors.putAll(addValidata(data, files));
        }

        data.forEach((key, value) -> {
            Map<String, String> rule = (Map)files.get(key);
            String regEx = (String)rule.get(RuleName.RegularExpression.getKey());
            if (StringUtil.isNotBlank(regEx)) {
                if (!isEq(value, regEx)) {
                    errors.put(key, getError(rule, RuleName.RegularExpression));
                }
            } else {
                if (((String)rule.get(RuleName.IsNull.getKey())).equals(0) && StringUtil.isBlank(value)) {
                    errors.put(key, getError(rule, RuleName.IsNull));
                }

                boolean inValueSet = false;
                String valueSet = (String)rule.get(RuleName.ValueSet.getKey());
                if (!StringUtil.isNotBlank(valueSet)) {
                    inValueSet = true;
                } else {
                    String[] valueSetArray = valueSet.split("\\|");
                    String[] var9 = valueSetArray;
                    int var10 = valueSetArray.length;

                    for(int var11 = 0; var11 < var10; ++var11) {
                        String s = var9[var11];
                        if (s.equals(value)) {
                            inValueSet = true;
                        }
                    }

                    if (!inValueSet) {
                        errors.put(key, getError(rule, RuleName.ValueSet));
                    }
                }

                int length = Integer.valueOf((String)rule.get(RuleName.Length.getKey()));
                if (length > 0 && inValueSet && value.getBytes().length > length) {
                    errors.put(key, getError(rule, RuleName.Length));
                }
            }

        });
        if (errors.size() > 0) {
            throw new ServiceException("数据校验错误", errors);
        }
    }

    private static boolean isEq(String str, String regEx) {
        Pattern pattern = Pattern.compile(regEx);
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }

    private static Map<String, Object> addValidata(Map<String, String> data, Map<String, Map<String, String>> files) {
        Map<String, Object> errors = new HashMap();
        files.forEach((key, map) -> {
            if (!data.containsKey(key) && ((String)map.get(RuleName.IsNull.getKey())).equals("0")) {
                errors.put(key, getError(map, RuleName.IsNull));
            }

        });
        return errors;
    }

    private static String getError(Map<String, String> rule, RuleName ruleName) {
        return (String)rule.get(RuleName.Name.getKey()) + ruleName.getValue();
    }
}

