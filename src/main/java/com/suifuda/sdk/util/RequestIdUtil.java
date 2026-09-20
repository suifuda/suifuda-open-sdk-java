package com.suifuda.sdk.util;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * 请求公共字段工具。
 *
 * @author 张丹峰
 * @since 2026/9/17
 */
public final class RequestIdUtil {

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private static final char[] HEX_CHARS = "0123456789ABCDEF".toCharArray();

    private RequestIdUtil() {
    }

    /**
     * 当前时间，格式：yyyy-MM-dd HH:mm:ss
     */
    public static String currentTimestamp() {
        return LocalDateTime.now(ZoneId.of("Asia/Shanghai")).format(DATE_TIME_FORMATTER);
    }

    /**
     * 生成 32 位十六进制随机串（16 字节）。
     */
    public static String generateNonce() {
        byte[] bytes = new byte[16];
        SECURE_RANDOM.nextBytes(bytes);
        char[] hex = new char[bytes.length * 2];
        for (int i = 0; i < bytes.length; i++) {
            int value = bytes[i] & 0xFF;
            hex[i * 2] = HEX_CHARS[value >>> 4];
            hex[i * 2 + 1] = HEX_CHARS[value & 0x0F];
        }
        return new String(hex);
    }
}
