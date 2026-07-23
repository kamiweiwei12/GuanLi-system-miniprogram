package com.inspection.exception;

/**
 * 业务异常
 * 用于 Service 层参数校验失败、业务规则不满足等场景
 */
public class BusinessException extends RuntimeException {

    private final int code;

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    public BusinessException(String message) {
        this(400, message);
    }

    public int getCode() {
        return code;
    }
}