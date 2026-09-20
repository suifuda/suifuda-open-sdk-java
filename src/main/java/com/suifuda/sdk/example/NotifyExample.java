package com.suifuda.sdk.example;

import com.suifuda.sdk.SfdClient;
import com.suifuda.sdk.auth.ResponseHeaders;
import com.suifuda.sdk.config.SfdConfig;
import com.suifuda.sdk.config.SignType;

import java.util.HashMap;
import java.util.Map;

import static com.suifuda.sdk.example.Config.*;

/**
 * 异步通知验签示例。
 *
 * @author 张丹峰
 * @since 2026/9/17
 */
public class NotifyExample {

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

        String body = "{\"orderNo\":\"202609160001\",\"state\":1}";

        Map<String, String> headers = new HashMap<>();
        headers.put(ResponseHeaders.NONCE, "593BEC0C930BF1AFEB40B4A08C8FB242");
        headers.put(ResponseHeaders.SIGN, "YTKrVMiN1xrCYVRS27D+946Wt4O2CpXI6jjIIpQD9RAYjfdXWSWJQgWJDw7SQX7+mxTbaCZ3yPu6C5RdsmoO4Mr/c+uIkbuh2jwfCJ5tZRr/HK8Q5b6ttYtk+jDJJe3MchgdDfJ/KHU44RenxTni49e/vHAymp9AyW8Qq7l2dUaHMgRWr0mnjkDymidD/2AXLnPIbUg1W3WWSBbudSg79NeIba/W88HfWWawXLuiryjjHCWxRG6mpSSrkAQJ8jmbacQLRWodFmqow3Mq1SNg8MdCF83IBlo+n0fsWmJJON+omCUaBlxsnYj6Ngw+JBScyfR+swHH6I1jKPBZOiO9Jg==");
        headers.put(ResponseHeaders.TIMESTAMP, "2026-09-16 10:30:00");
        headers.put(ResponseHeaders.SIGN_TYPE, "RSA");
        headers.put(ResponseHeaders.APP_KEY, "SFD_000001");

        boolean ok = client.verifyNotify(headers, body);
        System.out.println("notify verify: " + ok);
    }
}
