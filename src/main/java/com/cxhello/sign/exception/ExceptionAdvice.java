package com.cxhello.sign.exception;

import com.cxhello.sign.enums.StatusCodeEnum;
import com.cxhello.sign.util.Result;
import com.cxhello.sign.util.ResultUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author cxhello
 * @date 2021/7/29
 */
@RestControllerAdvice
public class ExceptionAdvice {

    private static final Logger logger = LoggerFactory.getLogger(ExceptionAdvice.class);

    @ExceptionHandler(value = BusinessException.class)
    public Result handle(BusinessException e) {
        return ResultUtils.fail(e.getMessage());
    }

    @ExceptionHandler(value = Exception.class)
    public Result handle(Exception e) {
        logger.error(e.getMessage(), e);
        return ResultUtils.result(StatusCodeEnum.INTERNAL_SERVER_ERROR.getCode(), "系统异常");
    }

}
