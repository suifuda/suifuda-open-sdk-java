package com.suifuda.sdk.example;

import com.suifuda.sdk.SfdClient;
import com.suifuda.sdk.config.SfdConfig;
import com.suifuda.sdk.config.SignType;
import com.suifuda.sdk.model.SfdHttpResponse;

import java.util.HashMap;
import java.util.Map;

import static com.suifuda.sdk.example.Config.*;

/**
 * 使用示例。
 *
 * @author 张丹峰
 * @since 2026/9/17
 */
public class PayExample {

    public static void main(String[] args) {

        SfdConfig sfdConfig = SfdConfig.builder()
                .baseUrl(BASE_URL)
                // 也可：.useTestEnv() / .useProdEnv()
                .appKey(APP_KEY)
                .privateKey(PRIVATE_KEY)
                .signType(SignType.fromCode(SIGN_TYPE))
                .platformPublicKey(PLATFORM_PUBLIC_KEY)
                .enableLog(true)
                .build();

        SfdClient client = SfdClient.create(sfdConfig);

        Map<String, Object> params = new HashMap<>();
        params.put("orderNo", System.currentTimeMillis() + "-test");
        params.put("authCode", "134567890123456789");

        SfdHttpResponse httpResponse = client.execute("/open/trade/v1/pay/barcode", params);
        System.out.println("HTTP Status: " + httpResponse.getStatusCode());
        System.out.println("Body: " + httpResponse.getBody());
    }
}
