package com.mwu.backend.common;

import java.io.Serializable;

public class BaseResponse <T> implements Serializable {
    private int Code;

    private T data;

    private String message;


    public BaseResponse(int code, T data, String message) {
        this.Code = code;
        this.data = data;
        this.message = message;
    }

    public BaseResponse(int code, T data) {
        this(code, data, "");
    }

    public BaseResponse( ErrorCode errorCode) {
        this(errorCode.getCode(), null, errorCode.getMessage());
    }




}
