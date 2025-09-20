package com.code.probationwork.handler;

import com.code.probationwork.exception.MyException;
import com.code.probationwork.result.AjaxResult;
import com.code.probationwork.util.HandlerUtils;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
@Order(50)
//自定义异常处理
public class MyExceptionHandler {
    @ExceptionHandler({
            MyException.class
    })
    @ResponseBody
    public AjaxResult<Object> handleMyException(MyException e) {
        HandlerUtils.logException(e);
        return AjaxResult.fail(e.getErrorCode(), e.getErrorMsg());
    }
}
