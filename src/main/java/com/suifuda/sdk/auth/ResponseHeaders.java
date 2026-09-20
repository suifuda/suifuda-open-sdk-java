package com.suifuda.sdk.auth;

/**
 * 开放平台响应头常量。
 *
 * @author 张丹峰
 * @since 2026/9/17
 */
public final class ResponseHeaders {

    public static final String NONCE = "Sfd-Nonce";
    public static final String SIGN = "Sfd-Sign";
    public static final String TIMESTAMP = "Sfd-Timestamp";
    public static final String SIGN_TYPE = "Sfd-Sign-Type";
    public static final String APP_KEY = "Sfd-App-Key";

    private ResponseHeaders() {
    }
}
