package com.code.probationwork.exception;

import com.code.probationwork.constant.ExceptionEnum;
import lombok.Data;

@Data
public class MyException extends RuntimeException {
    private final Integer errorCode;
    private final String errorMsg;

    public MyException(Integer errorCode, String errorMsg) {
        super(errorMsg);
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    public MyException(Integer errorCode, String errorMsg, Throwable cause) {
        super(errorMsg, cause);
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    public MyException(ExceptionEnum exceptionEnum) {
        super(exceptionEnum.getErrorMsg());
        this.errorCode = exceptionEnum.getErrorCode();
        this.errorMsg = exceptionEnum.getErrorMsg();
    }

    public MyException(ExceptionEnum exceptionEnum, Throwable cause) {
        super(exceptionEnum.getErrorMsg(), cause);
        this.errorCode = exceptionEnum.getErrorCode();
        this.errorMsg = exceptionEnum.getErrorMsg();
    }
}
