package com.sky.exception;

/**
 * 请求参数非法异常
 */
public class RequestParamInvalidException extends BaseException {
    public RequestParamInvalidException() {
    }

    public RequestParamInvalidException(String msg) {
        super(msg);
    }
}
