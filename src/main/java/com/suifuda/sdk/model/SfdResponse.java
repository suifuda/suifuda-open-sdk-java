package com.suifuda.sdk.model;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.suifuda.sdk.exception.SfdSdkException;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

/**
 * 开放平台业务响应体。
 * <pre>
 * {"code":200,"data":object,"message":"","timestamp":"2026-09-18 15:12:14"}
 * </pre>
 *
 * @author 张丹峰
 * @since 2026/9/19
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class SfdResponse {

    /**
     * 业务成功码。
     */
    public static final int SUCCESS_CODE = 200;

    private Integer code;
    private Object data;
    private String message;
    /**
     * 服务端时间，可能是字符串（yyyy-MM-dd HH:mm:ss）或数值时间戳。
     */
    private Object timestamp;

    /**
     * 从响应 Body JSON 解析。
     *
     * @param body 原始 JSON 字符串
     * @return 业务响应
     */
    public static SfdResponse fromJson(String body) {
        if (StringUtils.isBlank(body)) {
            throw SfdSdkException.of("PARSE_ERROR", "响应 body 为空，无法解析业务结果");
        }
        try {
            SfdResponse response = JSON.parseObject(body, SfdResponse.class);
            if (response == null) {
                throw SfdSdkException.of("PARSE_ERROR", "响应 body 不是合法业务 JSON");
            }
            return response;
        } catch (SfdSdkException e) {
            throw e;
        } catch (Exception e) {
            throw SfdSdkException.of("PARSE_ERROR", "解析业务响应失败: " + e.getMessage(), e);
        }
    }

    /**
     * 业务是否成功（code == {@link #SUCCESS_CODE}）。
     */
    public boolean isSuccess() {
        return code != null && code == SUCCESS_CODE;
    }

    /**
     * 将 data 转为指定类型。
     */
    public <T> T getDataAs(Class<T> type) {
        if (data == null) {
            return null;
        }
        return JSON.parseObject(JSON.toJSONString(data), type);
    }

    /**
     * 将 data 转为泛型类型（如 List、Map）。
     */
    public <T> T getDataAs(TypeReference<T> typeReference) {
        if (data == null) {
            return null;
        }
        return JSON.parseObject(JSON.toJSONString(data), typeReference);
    }

    /**
     * timestamp 字符串形式。
     */
    public String getTimestampAsString() {
        return timestamp == null ? null : String.valueOf(timestamp);
    }
}
