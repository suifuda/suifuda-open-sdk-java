package com.suifuda.sdk.util;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.SmUtil;
import cn.hutool.crypto.asymmetric.SM2;
import cn.hutool.crypto.asymmetric.Sign;
import cn.hutool.crypto.asymmetric.SignAlgorithm;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.suifuda.sdk.config.SfdConfig;
import com.suifuda.sdk.config.SignType;
import com.suifuda.sdk.exception.SfdSdkException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * 签名 / 验签工具。
 * <p>
 * 待签名字符串规则：
 * <ol>
 *   <li>排除 sign 参数</li>
 *   <li>排除值为 null 或空字符串的参数</li>
 *   <li>按参数名 ASCII 字典序排序</li>
 *   <li>以 key=value&amp; 格式拼接</li>
 * </ol>
 *
 * @author 张丹峰
 * @since 2026/9/17
 */
@Slf4j
public final class SignUtil {

    public static String sign(Object obj, String privateKey, SignType signType) {
        String jsonString = JSON.toJSONString(obj);
        Map<String, String> params = JSON.parseObject(jsonString, new TypeReference<Map<String, String>>() {
        });
        return sign(params, privateKey, signType);
    }

    public static String sign(Object obj, String privateKey, String signType) {
        return sign(obj, privateKey, SignType.fromCode(signType));
    }

    public static String sign(Map<String, String> params, String privateKey, SignType signType) {
        if (params == null || params.isEmpty()) {
            throw SfdSdkException.of("SIGN_ERROR", "签名参数不能为空");
        }
        if (StringUtils.isBlank(privateKey)) {
            throw SfdSdkException.of("SIGN_ERROR", "私钥不能为空");
        }
        if (signType == null) {
            throw SfdSdkException.of("SIGN_ERROR", "signType 不能为空");
        }

        String content = buildSignContent(params);
        switch (signType) {
            case RSA:
                return rsaSign(content, privateKey);
            case SM:
                return smSign(content, privateKey);
            default:
                throw SfdSdkException.of("SIGN_ERROR", "不支持的 signType: " + signType);
        }
    }

    public static String sign(Map<String, String> params, String privateKey, String signType) {
        return sign(params, privateKey, SignType.fromCode(signType));
    }

    public static boolean verify(Object obj, String signBase64, String publicKey, SignType signType) {
        String jsonString = JSON.toJSONString(obj);
        Map<String, String> params = JSON.parseObject(jsonString, new TypeReference<Map<String, String>>() {
        });
        return verify(params, signBase64, publicKey, signType);
    }

    public static boolean verify(Map<String, String> params, String signBase64, String publicKey, SignType signType) {
        if (params == null || params.isEmpty()) {
            return false;
        }
        if (StringUtils.isAnyBlank(signBase64, publicKey) || signType == null) {
            return false;
        }

        String content = buildSignContent(params);
        switch (signType) {
            case RSA:
                return rsaVerify(content, signBase64, publicKey);
            case SM:
                return smVerify(content, signBase64, publicKey);
            default:
                throw SfdSdkException.of("VERIFY_ERROR", "不支持的 signType: " + signType);
        }
    }

    public static boolean verify(Map<String, String> params, String signBase64, String publicKey, String signType) {
        return verify(params, signBase64, publicKey, SignType.fromCode(signType));
    }

    public static String rsaSign(String content, String privateKey) {
        try {
            Sign sign = SecureUtil.sign(SignAlgorithm.SHA256withRSA, KeyFormatUtil.normalize(privateKey), null);
            byte[] signedData = sign.sign(content.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(signedData);
        } catch (SfdSdkException e) {
            throw e;
        } catch (Exception e) {
            throw SfdSdkException.of("SIGN_ERROR",
                    "RSA 签名失败，请确认私钥为合法 PKCS#8 Base64（可带或不带 PEM 头尾）: " + e.getMessage(), e);
        }
    }

    public static String smSign(String content, String privateKey) {
        try {
            SM2 sm2 = SmUtil.sm2(KeyFormatUtil.normalize(privateKey), null);
            byte[] signedData = sm2.sign(content.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(signedData);
        } catch (SfdSdkException e) {
            throw e;
        } catch (Exception e) {
            throw SfdSdkException.of("SIGN_ERROR",
                    "SM2 签名失败，请确认私钥格式正确: " + e.getMessage(), e);
        }
    }

    public static boolean rsaVerify(String content, String signBase64, String publicKey) {
        try {
            Sign sign = SecureUtil.sign(SignAlgorithm.SHA256withRSA, null, KeyFormatUtil.normalize(publicKey));
            byte[] signature = Base64.getDecoder().decode(signBase64);
            return sign.verify(content.getBytes(StandardCharsets.UTF_8), signature);
        } catch (SfdSdkException e) {
            throw e;
        } catch (Exception e) {
            throw SfdSdkException.of("VERIFY_ERROR",
                    "RSA 验签失败，请确认公钥为合法 X.509 Base64: " + e.getMessage(), e);
        }
    }

    public static boolean smVerify(String content, String signBase64, String publicKey) {
        try {
            SM2 sm2 = SmUtil.sm2(null, KeyFormatUtil.normalize(publicKey));
            byte[] signature = Base64.getDecoder().decode(signBase64);
            return sm2.verify(content.getBytes(StandardCharsets.UTF_8), signature);
        } catch (SfdSdkException e) {
            throw e;
        } catch (Exception e) {
            throw SfdSdkException.of("VERIFY_ERROR",
                    "SM2 验签失败，请确认公钥格式正确: " + e.getMessage(), e);
        }
    }

    /**
     * 构造待签名字符串。
     */
    public static String buildSignContent(Map<String, String> params) {
        TreeMap<String, String> sortedMap = new TreeMap<>(params);
        String content = sortedMap.entrySet().stream()
                .filter(e -> !"sign".equals(e.getKey()))
                .filter(e -> StringUtils.isNotBlank(e.getValue()))
                .map(e -> e.getKey() + "=" + e.getValue())
                .collect(Collectors.joining("&"));
        return content;
    }
}
