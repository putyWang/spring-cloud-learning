package com.learning.validation.core.exception;

import com.learning.core.exception.LearningException;
import lombok.Data;

/**
 * @author WangWei
 * @version v 1.0
 * @description 验证错误
 * @date 2024-06-21
 **/
@Data
public class ValidException extends LearningException {

    public ValidException(String msg, Object data) {
        super(msg, data);
    }
}
