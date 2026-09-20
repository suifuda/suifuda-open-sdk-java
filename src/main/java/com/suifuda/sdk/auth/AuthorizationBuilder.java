package com.suifuda.sdk.auth;

import com.suifuda.sdk.config.SfdConfig;
import com.suifuda.sdk.config.SignType;
import com.suifuda.sdk.util.SignUtil;
import com.suifuda.sdk.util.RequestIdUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * Authorization 请求头构造器。
 * <p>
 * 格式：
 * <pre>
 * Authorization: SFD appKey="...",nonce="...",sign="...",signType="RSA",timestamp="..."
 * </pre>
 * <p>
 * 签名字段包含：appKey、timestamp、signType、nonce、data（请求体 JSON），
 * 其中 data 仅参与签名，不放入 Authorization。
 *
 * @author 张丹峰
 * @since 2026/9/17
 */
public final class AuthorizationBuilder {

    public static final String AUTH_SCHEME = "SFD";

    private AuthorizationBuilder() {
    }

    /**
     * 根据配置与请求体构造 Authorization 头。
     *
     * @param config    SDK 配置
     * @param bodyJson  业务请求体 JSON 字符串
     * @return Authorization 头完整值
     */
    public static String build(SfdConfig config, String bodyJson) {
        return build(
                config.getAppKey(),
                config.getPrivateKey(),
                config.getSignType(),
                bodyJson,
                RequestIdUtil.currentTimestamp(),
                RequestIdUtil.generateNonce()
        );
    }

    public static String build(String appKey,
                               String privateKey,
                               SignType signType,
                               String bodyJson,
                               String timestamp,
                               String nonce) {
        Map<String, String> signParams = new TreeMap<>();
        signParams.put("appKey", appKey);
        signParams.put("timestamp", timestamp);
        signParams.put("signType", signType.getCode());
        signParams.put("nonce", nonce);
        signParams.put("data", bodyJson == null ? "" : bodyJson);

        String sign = SignUtil.sign(signParams, privateKey, signType);
        signParams.put("sign", sign);

        String credential = signParams.entrySet().stream()
                .filter(e -> !"data".equals(e.getKey()))
                .filter(e -> StringUtils.isNotBlank(e.getValue()))
                .map(e -> e.getKey() + "=\"" + e.getValue() + "\"")
                .collect(Collectors.joining(","));

        return AUTH_SCHEME + " " + credential;
    }
}
