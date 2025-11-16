package io.sf.third.hqjh;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kong.unirest.Unirest;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
public class HqjhApiClient {

    @Autowired
    private HqjhProperties properties;

    public Map<String, String> generateHeaders(Map<String, Object> params) throws JsonProcessingException {
        Map<String, String> headers = new HashMap<>();

        // 添加必需请求头
        headers.put("apiKey", properties.getApiKey());
        headers.put("timestamp", String.valueOf(System.currentTimeMillis()));
        headers.put("nonce", UUID.randomUUID().toString());
        headers.put("mchNo", properties.getMchNo());
        // 生成签名
        String signature = SignUtils.generateSign(params, headers, properties.getApiSecret());
        headers.put("signature", signature);

        return headers;
    }

    // 调用示例
    public Map<String, Object> callApi(Long userId, String moduleKey) throws JsonProcessingException {
        // 准备业务参数
        Map<String, Object> params = new HashMap<>();
        params.put("serviceUserKey", "sk_test_" + userId);
        params.put("payUrl", "https://cusj.com");
        params.put("expireUrl", "https://dhdjs.com/expire");
        params.put("mchUuidNo", "M1763027684499");
        params.put("moduleKey", moduleKey);

        // 生成签名头
        Map<String, String> headers = generateHeaders(params);
        headers.put("Content-Type", "application/json");
        // 发送请求（请使用您选择的HTTP客户端）
        // 注意：同时传递params和headers
        var response = Unirest.post(properties.getBaseUrl() + "/h5/api/commonUrl").headers(headers)
                .body(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(params)).asJson();
        log.info(response.getBody().toString());
        return response.getBody().getObject().toMap();
    }

}
