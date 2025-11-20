package io.sf.third.hqjh;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kong.unirest.Unirest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
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
    public Map<String, Object> callApi(String url,Map<String,Object> params) throws JsonProcessingException {
        // 生成签名头
        Map<String, String> headers = generateHeaders(params);
        headers.put("Content-Type", "application/json");
        // 发送请求（请使用您选择的HTTP客户端）
        // 注意：同时传递params和headers
        var response = Unirest.post(properties.getBaseUrl() + url).headers(headers)
                .body(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(params)).asJson();
        log.info(response.getBody().toString());
        return response.getBody().getObject().toMap();
    }

    public Map<String, Object> getUrl(Long userId, HqjhGetUrlRequest request) throws JsonProcessingException {
        request.setMchUuidNo(properties.getMchNo());
        request.setServiceUserKey(String.format("sk_local_test_%d",userId));
        return callApi("/h5/api/commonUrl", request.toMap());
    }


}
