package com.suifuda.sdk;

import com.alibaba.fastjson2.JSON;
import com.suifuda.sdk.auth.AuthorizationBuilder;
import com.suifuda.sdk.auth.ResponseHeaders;
import com.suifuda.sdk.auth.ResponseVerifier;
import com.suifuda.sdk.config.SfdConfig;
import com.suifuda.sdk.exception.SfdSdkException;
import com.suifuda.sdk.http.HttpExecutor;
import com.suifuda.sdk.model.SfdHttpResponse;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 随付达开放平台 SDK 客户端。
 * <p>
 * 线程安全，建议全局单例复用。
 *
 * <pre>{@code
 * SfdConfig sfdConfig = SfdConfig.builder()
 *     .useTestEnv()
 *     .appKey("your_app_key")
 *     .privateKey("your_private_key")
 *     .signType(SignType.RSA)
 *     .platformPublicKey("platform_public_key")
 *     .build();
 * SfdClient client = SfdClient.create(sfdConfig);
 *
 * SfdHttpResponse httpResponse = client.execute("/open/trade/v1/pay/barcode", params);
 * // params 为 Map，例如包含 orderNo、authCode
 * SfdResponse response = httpResponse.getResponse();
 * }</pre>
 *
 * @author 张丹峰
 * @since 2026/9/17
 */
public class SfdClient {

    @Getter
    private final SfdConfig config;
    private final HttpExecutor httpExecutor;

    public SfdClient(SfdConfig config) {
        this.config = Objects.requireNonNull(config, "config");
        this.httpExecutor = new HttpExecutor(config);
    }


    public static SfdClient create(SfdConfig config) {
        return new SfdClient(config);
    }



    /**
     * POST JSON，并返回完整 HTTP 响应（按配置自动验签）。
     */
    public SfdHttpResponse execute(String path, Object body) {
        String url = resolveUrl(path);
        String bodyJson = toJson(body);
        String authorization = AuthorizationBuilder.build(config, bodyJson);
        return afterResponse(httpExecutor.postJson(url, bodyJson, authorization));
    }

    /**
     * 仅构造 Authorization 头（便于自定义 HTTP 客户端）。
     */
    public String buildAuthorization(Object body) {
        return AuthorizationBuilder.build(config, toJson(body));
    }

    /**
     * 仅构造 Authorization 头。
     */
    public String buildAuthorization(String bodyJson) {
        return AuthorizationBuilder.build(config, bodyJson);
    }

    /**
     * 手动校验同步响应签名。
     */
    public boolean verifyResponse(SfdHttpResponse httpResponse) {
        return ResponseVerifier.verify(config.getPlatformPublicKey(), httpResponse);
    }

    /**
     * 校验平台异步通知签名。
     * <p>
     * 通知格式与同步响应一致：签名在请求头，业务数据在 Body。
     * <pre>
     * Sfd-Nonce / Sfd-Sign / Sfd-Timestamp / Sfd-Sign-Type / Sfd-App-Key
     * Body: {"orderNo":"202609160001","state":1}
     * </pre>
     *
     * @param headers 通知请求头（可为多值 Map，或单值 Map）
     * @param body    通知原始 Body（建议直接使用原始 JSON 字符串）
     * @return true 表示验签通过
     */
    public boolean verifyNotify(Map<String, String> headers, String body) {
        return verifyNotify(config.getPlatformPublicKey(), headers, body);
    }

    /**
     * 校验平台异步通知签名（封装为 {@link SfdHttpResponse}）。
     */
    public boolean verifyNotify(SfdHttpResponse notifyRequest) {
        return ResponseVerifier.verify(config.getPlatformPublicKey(), notifyRequest);
    }

    /**
     * 校验平台异步通知签名（指定平台公钥）。
     */
    public static boolean verifyNotify(String platformPublicKey,
                                       Map<String, ?> headers,
                                       String body) {
        if (StringUtils.isBlank(platformPublicKey)) {
            throw SfdSdkException.of("VERIFY_ERROR", "platformPublicKey 不能为空");
        }
        SfdHttpResponse notify = toNotifyResponse(headers, body);
        return ResponseVerifier.verify(platformPublicKey, notify);
    }

    /**
     * 校验平台异步通知签名（按头字段分别传入）。
     */
    public boolean verifyNotify(String appKey,
                                String nonce,
                                String sign,
                                String timestamp,
                                String signType,
                                String body) {
        Map<String, String> headers = new HashMap<String, String>(4);
        headers.put(ResponseHeaders.APP_KEY, appKey);
        headers.put(ResponseHeaders.NONCE, nonce);
        headers.put(ResponseHeaders.SIGN, sign);
        headers.put(ResponseHeaders.TIMESTAMP, timestamp);
        headers.put(ResponseHeaders.SIGN_TYPE, signType);
        return verifyNotify(headers, body);
    }

    private SfdHttpResponse afterResponse(SfdHttpResponse httpResponse) {
        if (!config.isEnableResponseVerify()) {
            return httpResponse;
        }
        boolean verified = ResponseVerifier.verifyIfPresentOrThrow(
                config.getPlatformPublicKey(),
                httpResponse
        );
        return verified ? httpResponse.withSignVerified(true) : httpResponse;
    }

    private String resolveUrl(String path) {
        if (StringUtils.isBlank(path)) {
            throw SfdSdkException.of("path 不能为空");
        }
        if (path.startsWith("http://") || path.startsWith("https://")) {
            return path;
        }
        if (path.startsWith("/")) {
            return config.getBaseUrl() + path;
        }
        return config.getBaseUrl() + "/" + path;
    }

    private static String toJson(Object body) {
        if (body == null) {
            return "{}";
        }
        if (body instanceof String) {
            return (String) body;
        }
        return JSON.toJSONString(body);
    }

    @SuppressWarnings("unchecked")
    private static SfdHttpResponse toNotifyResponse(Map<String, ?> headers, String body) {
        Map<String, List<String>> multiHeaders = new HashMap<String, List<String>>();
        if (headers != null) {
            for (Map.Entry<String, ?> entry : headers.entrySet()) {
                if (entry.getKey() == null || entry.getValue() == null) {
                    continue;
                }
                Object value = entry.getValue();
                if (value instanceof List) {
                    List<?> list = (List<?>) value;
                    List<String> values = new ArrayList<String>(list.size());
                    for (Object item : list) {
                        if (item != null) {
                            values.add(String.valueOf(item));
                        }
                    }
                    multiHeaders.put(entry.getKey(), values);
                } else {
                    multiHeaders.put(entry.getKey(), Collections.singletonList(String.valueOf(value)));
                }
            }
        }
        return new SfdHttpResponse(200, body == null ? "" : body, multiHeaders);
    }
}
