# 随付达开放平台 Java SDK

对外对外 API 调用的官方风格 Java SDK，封装请求签名、Authorization 头构造与 HTTP 调用。

## 环境要求

- JDK 8+
- Maven 3.6+

## 快速开始

### 1. 引入依赖

本地安装：

```bash
mvn clean install
```

```xml
<dependency>
    <groupId>com.suifuda</groupId>
    <artifactId>suifuda-open-sdk</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 2. 创建客户端

```java
import com.suifuda.sdk.SfdClient;
import com.suifuda.sdk.config.SignType;
import com.suifuda.sdk.config.SfdConfig;

SfdConfig sfdConfig = SfdConfig.builder()
        .useTestEnv()                          // 或 .useProdEnv()
        .appKey("YOUR_APP_KEY")
        .privateKey("YOUR_PRIVATE_KEY")        // Base64，不含 PEM 头尾
        .signType(SignType.RSA)                // 或 SignType.SM
        .platformPublicKey("PLATFORM_PUBLIC_KEY") // 响应验签 / 异步通知验签
        .enableLog(true)                       // 是否打印请求/响应日志，默认 false
        .enableResponseVerify(true)            // 是否校验响应签名，默认 true
        .build();
SfdClient client = SfdClient.create(sfdConfig);
```

### 3. 发起业务请求

```java
Map<String, Object> params = new HashMap<>();
params.put("orderNo", "20260917001");
params.put("authCode", "134567890123456789");

SfdHttpResponse httpResponse = client.execute("/open/trade/v1/pay/barcode", params);
// getResponse() 仅允许 HTTP 2xx；开启验签时 2xx 必须验签通过
SfdResponse response = httpResponse.getResponse();
if (response.isSuccess()) {
    // response.getData() / response.getDataAs(Xxx.class)
}
```

### 4. 异步通知验签

异步通知与同步响应验签规则相同：签名在请求头，Body 为业务 JSON。

```http
Sfd-Nonce: 593BEC0C930BF1AFEB40B4A08C8FB242
Sfd-Sign: <BASE64_SIGNATURE>
Sfd-Timestamp: 2026-09-16 10:30:00
Sfd-Sign-Type: RSA
Sfd-App-Key: SFD_000001

{"orderNo":"202609160001","state":1}
```

```java
// 方式一：headers + 原始 body
boolean ok = client.verifyNotify(requestHeaders, rawBody);

// 方式二：按字段传入
boolean ok2 = client.verifyNotify(appKey, nonce, sign, timestamp, signType, rawBody);
```

验签通过后再解析 Body 做业务处理；Body 请使用 HTTP 原始字符串，勿重新序列化。

## 请求约定

### Header

```http
Authorization: SFD appKey="YOUR_APP_KEY",nonce="...",sign="...",signType="RSA",timestamp="yyyy-MM-dd HH:mm:ss"
Content-Type: application/json
```

### Body

业务参数 JSON。

### 签名规则

参与签名字段：

| 字段 | 说明 |
|------|------|
| appKey | 应用标识 |
| timestamp | 请求时间，格式 `yyyy-MM-dd HH:mm:ss` |
| signType | `RSA` 或 `SM` |
| nonce | 32 位十六进制随机串 |
| data | 请求体 JSON 原文 |

规则：

1. 排除 `sign`
2. 排除空值
3. 按参数名 ASCII 字典序排序
4. 拼成 `key=value&key=value`
5. RSA 使用 `SHA256withRSA`，SM 使用 SM2（默认 userId：`1234567812345678`）
6. 签名结果做 Base64

`data` 仅参与签名，不放入 Authorization。

## 响应验签

响应头：

```http
Sfd-Nonce: 593BEC0C930BF1AFEB40B4A08C8FB242
Sfd-Sign: <BASE64_SIGNATURE>
Sfd-Timestamp: 2026-09-16 10:30:00
Sfd-Sign-Type: RSA
Sfd-App-Key: SFD_000001
```

验签字段与请求对称：`appKey` + `timestamp` + `signType` + `nonce` + `data`（响应体 JSON 原文），使用平台公钥校验 `Sfd-Sign`。

SDK 默认开启响应验签（需配置 `platformPublicKey`）。HTTP 2xx 必须带完整签名头并验签通过，否则抛出 `SfdSdkException`；非 2xx 无签名头（如 HTTP 400）则跳过。临时关闭：

```java
.enableResponseVerify(false)
```

手动验签：

```java
boolean ok = client.verifyResponse(response);
```

## 目录结构

```text
com.suifuda.sdk
├── SfdClient                 # SDK 入口
├── auth.AuthorizationBuilder # Authorization 构造
├── auth.ResponseVerifier     # 响应验签
├── config.SfdConfig          # 配置
├── config.SignType           # RSA / SM
├── util.SignUtil             # 签名验签
├── http.HttpExecutor         # HTTP 执行
├── model.SfdHttpResponse     # HTTP 响应封装
├── model.SfdResponse      # 业务响应体 code/data/message/timestamp
├── exception.SfdSdkException # 异常
└── example.PayExample        # 示例
```

## 环境地址

| 环境 | 方法 | Base URL |
|------|------|----------|
| 测试 | `useTestEnv()` | `https://open-api.test.suifuda.com` |
| 生产 | `useProdEnv()` | `https://open-api.suifuda.com` |

## 自定义 HTTP

如需自行发请求，可只使用签名能力：

```java
String authorization = client.buildAuthorization(params);
// 自行设置 Header: Authorization / Content-Type，再 POST JSON body
```
