package com.suifuda.sdk.exception;

import lombok.Getter;

/**
 * SDK 统一异常。
 *
 * @author 张丹峰
 * @since 2026/9/17
 */
@Getter
public class SfdSdkException extends RuntimeException {

    private static final long serialVersionUID = -8904246990767358084L;

    private final String code;

    public SfdSdkException(String message) {
        this("SDK_ERROR", message);
    }

    public SfdSdkException(String code, String message) {
        super(message);
        this.code = code;
    }

    public SfdSdkException(String message, Throwable cause) {
        this("SDK_ERROR", message, cause);
    }

    public SfdSdkException(String code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public static SfdSdkException of(String message) {
        return new SfdSdkException(message);
    }

    public static SfdSdkException of(String code, String message) {
        return new SfdSdkException(code, message);
    }

    public static SfdSdkException of(String code, String message, Throwable cause) {
        return new SfdSdkException(code, message, cause);
    }
}
