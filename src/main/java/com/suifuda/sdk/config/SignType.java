package com.suifuda.sdk.config;

import com.suifuda.sdk.exception.SfdSdkException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 签名算法类型。
 *
 * @author 张丹峰
 * @since 2026/9/17
 */
@Getter
@RequiredArgsConstructor
public enum SignType {

    /**
     * RSA（SHA256withRSA）
     */
    RSA("RSA"),

    /**
     * 国密 SM2
     */
    SM("SM");

    private final String code;

    public static SignType fromCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            throw new SfdSdkException("signType 不能为空");
        }
        for (SignType type : values()) {
            if (type.code.equalsIgnoreCase(code.trim())) {
                return type;
            }
        }
        throw new SfdSdkException("不支持的 signType: " + code);
    }
}
