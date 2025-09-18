package com.mwu.backend.exception;

import com.mwu.backend.common.BaseResponse;
import com.mwu.backend.common.ErrorCode;
import com.mwu.backend.common.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public BaseResponse<?> runtimeExceptionHandler(RuntimeException e) {
        log.info(e.getMessage(),e);
        return ResultUtils.error(ErrorCode.OPERATION_ERROR, e.getMessage());

    }
}
