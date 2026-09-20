package com.suifuda.sdk.model;

import com.suifuda.sdk.auth.ResponseHeaders;
import com.suifuda.sdk.auth.ResponseVerifier;
import com.suifuda.sdk.exception.SfdSdkException;
import lombok.Getter;
import lombok.ToString;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * HTTP 响应封装。
 *
 * @author 张丹峰
 * @since 2026/9/17
 */
@Getter
@ToString
public class SfdHttpResponse {

    private final int statusCode;
    private final String body;
    private final Map<String, List<String>> headers;
    private final boolean signVerified;

    public SfdHttpResponse(int statusCode, String body, Map<String, List<String>> headers) {
        this(statusCode, body, headers, false);
    }

    public SfdHttpResponse(int statusCode, String body, Map<String, List<String>> headers, boolean signVerified) {
        this.statusCode = statusCode;
        this.body = body;
        this.headers = headers == null ? Collections.emptyMap() : Collections.unmodifiableMap(headers);
        this.signVerified = signVerified;
    }

    public boolean isOk() {
        return statusCode >= 200 && statusCode < 300;
    }

    public String getHeader(String name) {
        return ResponseVerifier.firstHeader(headers, name);
    }

    public String getNonce() {
        return getHeader(ResponseHeaders.NONCE);
    }

    public String getSign() {
        return getHeader(ResponseHeaders.SIGN);
    }

    public String getTimestamp() {
        return getHeader(ResponseHeaders.TIMESTAMP);
    }

    public String getSignType() {
        return getHeader(ResponseHeaders.SIGN_TYPE);
    }

    public String getAppKey() {
        return getHeader(ResponseHeaders.APP_KEY);
    }

    /**
     * 将 body 解析为业务响应模型。
     * <p>
     * 仅允许在 HTTP 2xx 时调用。非 2xx（如网关 500）可能无签名头，
     */
    public SfdResponse getResponse() {
        if (!isOk()) {
            throw SfdSdkException.of("HTTP_ERROR",
                    "HTTP 状态码非 2xx（" + statusCode + "），拒绝解析业务响应，请检查 getBody()");
        }
        return SfdResponse.fromJson(body);
    }

    /**
     * 复制并标记验签结果。
     */
    public SfdHttpResponse withSignVerified(boolean verified) {
        return new SfdHttpResponse(statusCode, body, headers, verified);
    }
}
