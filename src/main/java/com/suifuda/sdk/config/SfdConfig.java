package com.suifuda.sdk.config;

import com.suifuda.sdk.exception.SfdSdkException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.time.Duration;
import java.util.Objects;

/**
 * SDK 配置（不可变）。
 *
 * @author 张丹峰
 * @since 2026/9/17
 */
@Getter
public final class SfdConfig {

    /**
     * 测试环境
     */
    public static final String TEST_BASE_URL = "https://open-api.test.suifuda.com";

    /**
     * 生产环境
     */
    public static final String PROD_BASE_URL = "https://open-api.suifuda.com";

    /**
     * 基础请求地址
     */
    private final String baseUrl;

    /**
     * 应用标识
     */
    private final String appKey;

    /**
     * 应用私钥
     */
    private final String privateKey;

    /**
     * 平台公钥
     */
    private final String platformPublicKey;

    /**
     * 签名算法类型：RSA,SM
     */
    private final SignType signType;

    /**
     * 连接超时时间：毫秒
     */
    private final int connectTimeoutMs;

    /**
     * 响应超时时间：毫秒
     */
    private final int readTimeoutMs;

    /**
     * 日志打印开关
     */
    private final boolean enableLog;

    /**
     * 响应验签开关
     */
    private final boolean enableResponseVerify;

    private SfdConfig(Builder builder) {
        this.baseUrl = trimTrailingSlash(builder.baseUrl);
        this.appKey = builder.appKey;
        this.privateKey = builder.privateKey;
        this.platformPublicKey = builder.platformPublicKey;
        this.signType = builder.signType;
        this.connectTimeoutMs = builder.connectTimeoutMs;
        this.readTimeoutMs = builder.readTimeoutMs;
        this.enableLog = builder.enableLog;
        this.enableResponseVerify = builder.enableResponseVerify;
    }

    public static Builder builder() {
        return new Builder();
    }

    private static String trimTrailingSlash(String url) {
        if (url == null) {
            return null;
        }
        return url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class Builder {
        private String baseUrl = PROD_BASE_URL;
        private String appKey;
        private String privateKey;
        private String platformPublicKey;
        private SignType signType = SignType.RSA;
        private int connectTimeoutMs = 10_000;
        private int readTimeoutMs = 30_000;
        private boolean enableLog = false;
        private boolean enableResponseVerify = true;

        public Builder baseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }

        public Builder useTestEnv() {
            this.baseUrl = TEST_BASE_URL;
            return this;
        }

        public Builder useProdEnv() {
            this.baseUrl = PROD_BASE_URL;
            return this;
        }

        public Builder appKey(String appKey) {
            this.appKey = appKey;
            return this;
        }

        public Builder privateKey(String privateKey) {
            this.privateKey = privateKey;
            return this;
        }

        public Builder platformPublicKey(String platformPublicKey) {
            this.platformPublicKey = platformPublicKey;
            return this;
        }

        public Builder signType(SignType signType) {
            this.signType = signType;
            return this;
        }

        public Builder signType(String signType) {
            this.signType = SignType.fromCode(signType);
            return this;
        }

        public Builder connectTimeout(Duration timeout) {
            this.connectTimeoutMs = (int) Objects.requireNonNull(timeout, "connectTimeout").toMillis();
            return this;
        }

        public Builder readTimeout(Duration timeout) {
            this.readTimeoutMs = (int) Objects.requireNonNull(timeout, "readTimeout").toMillis();
            return this;
        }

        public Builder connectTimeoutMs(int connectTimeoutMs) {
            this.connectTimeoutMs = connectTimeoutMs;
            return this;
        }

        public Builder readTimeoutMs(int readTimeoutMs) {
            this.readTimeoutMs = readTimeoutMs;
            return this;
        }

        public Builder enableLog(boolean enableLog) {
            this.enableLog = enableLog;
            return this;
        }

        public Builder enableResponseVerify(boolean enableResponseVerify) {
            this.enableResponseVerify = enableResponseVerify;
            return this;
        }

        public SfdConfig build() {
            if (StringUtils.isBlank(baseUrl)) {
                throw SfdSdkException.of("baseUrl 不能为空");
            }
            if (StringUtils.isBlank(appKey)) {
                throw SfdSdkException.of("appKey 不能为空");
            }
            if (StringUtils.isBlank(privateKey)) {
                throw SfdSdkException.of("privateKey 不能为空");
            }
            if (signType == null) {
                throw SfdSdkException.of("signType 不能为空");
            }
            if (connectTimeoutMs <= 0 || readTimeoutMs <= 0) {
                throw SfdSdkException.of("超时时间必须大于 0");
            }
            if (enableResponseVerify && StringUtils.isBlank(platformPublicKey)) {
                throw SfdSdkException.of("开启响应验签时必须配置 platformPublicKey");
            }
            return new SfdConfig(this);
        }
    }
}
