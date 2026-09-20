package com.suifuda.sdk.util;

import com.suifuda.sdk.exception.SfdSdkException;
import org.apache.commons.lang3.StringUtils;

/**
 * 密钥格式处理。
 * <p>
 * 支持：
 * <ul>
 *   <li>纯 Base64（PKCS#8 / X.509）</li>
 *   <li>PEM（含 -----BEGIN ...----- 头尾）</li>
 * </ul>
 *
 * @author 张丹峰
 * @since 2026/9/17
 */
public final class KeyFormatUtil {

    private KeyFormatUtil() {
    }

    /**
     * 规范化密钥：去除 PEM 头尾、空白字符，得到纯 Base64。
     */
    public static String normalize(String key) {
        if (StringUtils.isBlank(key)) {
            throw SfdSdkException.of("KEY_ERROR", "密钥不能为空");
        }
        String trimmed = key.trim();
        if (trimmed.contains("BEGIN RSA PRIVATE KEY") || trimmed.contains("BEGIN RSA PUBLIC KEY")) {
            throw SfdSdkException.of("KEY_ERROR",
                    "不支持 PKCS#1（BEGIN RSA PRIVATE/PUBLIC KEY），请使用 PKCS#8/X.509（BEGIN PRIVATE/PUBLIC KEY）或对应纯 Base64");
        }
        String normalized = trimmed
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replace("-----BEGIN RSA PRIVATE KEY-----", "")
                .replace("-----END RSA PRIVATE KEY-----", "")
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replace("-----BEGIN RSA PUBLIC KEY-----", "")
                .replace("-----END RSA PUBLIC KEY-----", "")
                .replaceAll("\\s+", "");

        if (StringUtils.isBlank(normalized)) {
            throw SfdSdkException.of("KEY_ERROR", "密钥内容无效");
        }
        if (isPlaceholder(normalized)) {
            throw SfdSdkException.of("KEY_ERROR",
                    "请将示例中的占位密钥替换为真实 Base64 私钥（不含 PEM 头尾），当前值: " + key);
        }
        return normalized;
    }

    private static boolean isPlaceholder(String key) {
        String upper = key.toUpperCase();
        return upper.contains("YOUR_")
                || upper.contains("YOURPRIVATE")
                || upper.contains("YOURPUBLIC")
                || "PRIVATE_KEY".equals(upper)
                || "PUBLIC_KEY".equals(upper);
    }
}
