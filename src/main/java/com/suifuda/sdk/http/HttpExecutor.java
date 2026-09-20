package com.suifuda.sdk.http;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.suifuda.sdk.config.SfdConfig;
import com.suifuda.sdk.exception.SfdSdkException;
import com.suifuda.sdk.model.SfdHttpResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * HTTP 执行器。
 *
 * @author 张丹峰
 * @since 2026/9/17
 */
@Slf4j
@RequiredArgsConstructor
public class HttpExecutor {

    private final SfdConfig config;

    public SfdHttpResponse postJson(String url, String body, String authorization) {
        Map<String, String> headers = new HashMap<>(4);
        headers.put("Authorization", authorization);
        headers.put("Content-Type", "application/json;charset=UTF-8");
        headers.put("Accept", "application/json");

        if (config.isEnableLog()) {
            log.info("SFD 请求地址：{}", url);
            log.info("SFD 请求头：{}", headers);
            log.info("SFD 请求参数：{}", body);
        }

        try (HttpResponse response = HttpRequest.post(url)
                .headerMap(headers, true)
                .body(body)
                .setConnectionTimeout(config.getConnectTimeoutMs())
                .setReadTimeout(config.getReadTimeoutMs())
                .execute()) {

            SfdHttpResponse result = new SfdHttpResponse(
                    response.getStatus(),
                    response.body(),
                    response.headers()
            );

            if (config.isEnableLog()) {
                log.info("SFD 响应状态：{}", result.getStatusCode());
                log.info("SFD 响应内容：{}", result.getBody());
                log.info("SFD 响应头：{}", result.getHeaders());
            }
            return result;
        } catch (SfdSdkException e) {
            throw e;
        } catch (Exception e) {
            if (config.isEnableLog()) {
                log.error("SFD 请求失败：{}", e.getMessage(), e);
            }
            throw SfdSdkException.of("HTTP_ERROR", "请求失败: " + e.getMessage(), e);
        }
    }
}
