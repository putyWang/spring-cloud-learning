package com.learning.job.schedule.core.conf;

import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Log4j2
public class GlobalExceptionHandler {

    public GlobalExceptionHandler() {
    }

    @ExceptionHandler({ServiceException.class})
    public PublicResult handleDiyException(ServiceException e) {
        return PublicResult.build(e.getCode(), e.getMsg(), e.getData());
    }

    @ExceptionHandler({XxlJobException.class})
    public PublicResult handleDiyException(XxlJobException e) {
        return PublicResult.build(ResultStatus.FAILED.getValue(), e.getMessage(), (Object)null);
    }

    @ExceptionHandler({BindException.class, MethodArgumentNotValidException.class})
    public PublicResult validationExceptionHandler(Exception e) {
        log.warn(e.getMessage());
        List<FieldError> fieldErrors = new ArrayList(10);
        if (e instanceof BindException) {
            fieldErrors = ((BindException)e).getBindingResult().getFieldErrors();
        } else if (e instanceof MethodArgumentNotValidException) {
            fieldErrors = ((MethodArgumentNotValidException)e).getBindingResult().getFieldErrors();
        }

        Map<String, String> map = new HashMap(10);
        Iterator var4 = ((List)fieldErrors).iterator();

        while(var4.hasNext()) {
            FieldError error = (FieldError)var4.next();
            map.put(error.getField(), error.getDefaultMessage());
        }

        return PublicResult.build(ResultStatus.FAILED_DATA_VALIDATE.getValue(), ResultStatus.FAILED_DATA_VALIDATE.getReasonPhrase(), (Object)null);
    }

    @ExceptionHandler({Exception.class})
    public PublicResult handleException(Exception e) {
        log.error("接口调用异常", e);
        return PublicResult.failed();
    }
}
