package com.learning.validation.config;

import com.learning.validation.aspect.ValidationAspect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author WangWei
 * @version v 1.0
 * @description
 * @date 2024-06-21
 **/
@Configuration
public class ValidationConfig {
    public ValidationConfig() {
    }

    @Bean
    public ValidationAspect validationAspect() {
        return new ValidationAspect();
    }
}
