package com.suifuda.sdk.auth;

import com.suifuda.sdk.config.SignType;
import com.suifuda.sdk.exception.SfdSdkException;
import com.suifuda.sdk.model.SfdHttpResponse;
import com.suifuda.sdk.util.SignUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * 响应验签。
 * <p>
 * 签名字段与请求对称：
 * appKey、timestamp、signType、nonce、data（响应体 JSON 原文），
 * 其中 data 仅参与签名；签名值取自响应头 {@code Sfd-Sign}，
 * 使用平台公钥验签。
 * <p>
 * HTTP 2xx 成功响应必须带完整签名头并验签通过；
 * 部分错误响应（如 HTTP 400）可能不带签名头，此时跳过验签。
 *
 * @author 张丹峰
 * @since 2026/9/17
 */
@Slf4j
public final class ResponseVerifier {

    private ResponseVerifier() {
    }

    /**
     * 响应是否包含完整签名头。
     */
    public static boolean hasSignHeaders(SfdHttpResponse httpResponse) {
        if (httpResponse == null) {
            return false;
        }
        return StringUtils.isNoneBlank(
                httpResponse.getNonce(),
                httpResponse.getSign(),
                httpResponse.getTimestamp(),
                httpResponse.getSignType(),
                httpResponse.getAppKey()
        );
    }

    /**
     * 校验响应签名。
     * <p>
     * 无签名头时返回 false（不抛异常）；有签名头时按规则验签。
     *
     * @param platformPublicKey 平台公钥
     * @param httpResponse          HTTP 响应
     * @return true 表示验签通过
     */
    public static boolean verify(String platformPublicKey, SfdHttpResponse httpResponse) {
        if (httpResponse == null) {
            throw SfdSdkException.of("VERIFY_ERROR", "响应不能为空");
        }
        if (!hasSignHeaders(httpResponse)) {
            return false;
        }
        if (StringUtils.isBlank(platformPublicKey)) {
            throw SfdSdkException.of("VERIFY_ERROR", "未配置 platformPublicKey，无法验签");
        }

        String nonce = httpResponse.getNonce();
        String sign = httpResponse.getSign();
        String timestamp = httpResponse.getTimestamp();
        String signTypeCode = httpResponse.getSignType();
        String appKey = httpResponse.getAppKey();

        Map<String, String> signParams = new TreeMap<>();
        signParams.put("appKey", appKey);
        signParams.put("timestamp", timestamp);
        signParams.put("signType", signTypeCode);
        signParams.put("nonce", nonce);
        signParams.put("data", httpResponse.getBody() == null ? "" : httpResponse.getBody());

        return SignUtil.verify(signParams, sign, platformPublicKey, SignType.fromCode(signTypeCode));
    }

    /**
     * HTTP 2xx：必须有完整签名头且验签通过，否则抛异常。
     * 非 2xx：有签名头则验签，无签名头则跳过（如 400 错误响应）。
     *
     * @return true 表示已验签通过；false 表示非 2xx 且无签名头已跳过
     */
    public static boolean verifyIfPresentOrThrow(String platformPublicKey,
                                                 SfdHttpResponse httpResponse) {
        if (!hasSignHeaders(httpResponse)) {
            if (httpResponse != null && httpResponse.isOk()) {
                throw SfdSdkException.of("VERIFY_ERROR", "HTTP 2xx 响应缺少签名头，无法验签");
            }
            return false;
        }
        if (!verify(platformPublicKey, httpResponse)) {
            throw SfdSdkException.of("VERIFY_ERROR", "响应验签失败");
        }
        return true;
    }

    /**
     * 从多值响应头中按忽略大小写取第一个值。
     */
    public static String firstHeader(Map<String, List<String>> headers, String name) {
        if (headers == null || headers.isEmpty() || StringUtils.isBlank(name)) {
            return null;
        }
        for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
            if (entry.getKey() != null && entry.getKey().equalsIgnoreCase(name)) {
                List<String> values = entry.getValue();
                if (values != null && !values.isEmpty() && StringUtils.isNotBlank(values.get(0))) {
                    return values.get(0);
                }
                return null;
            }
        }
        return null;
    }
}
