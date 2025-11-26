package io.sf.third.hqjh;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;

import kong.unirest.Unirest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
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
    public byte[] callApi(String url,Map<String,Object> params) throws JsonProcessingException {
        // 生成签名头
        Map<String, String> headers = generateHeaders(params);
        headers.put("Content-Type", "application/json");
        // 发送请求（请使用您选择的HTTP客户端）
        // 注意：同时传递params和headers
        var response = Unirest.post(properties.getBaseUrl() + url).headers(headers)
                .body(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(params)).asBytes();
        log.info(response.getBody().toString());
        return response.getBody();
    }

    public HqjhClientResponse<HqjhClientResponse.GetUrlResponseData> getUrl(Long userId, HqjhGetUrlRequest request) throws IOException,StreamReadException,DatabindException {
        request.setMchUuidNo(properties.getMchNo());
        request.setServiceUserKey(String.format("sk_local_test_%d",userId));
        var response = callApi("/h5/api/commonUrl", request.toMap());
        var result = new ObjectMapper().readValue(response, new TypeReference<HqjhClientResponse<HqjhClientResponse.GetUrlResponseData>>() {});
        return result;
    }



    // {
    //     "code": 200,
    //         "msg": null,
    //         "data": {
    //             "url": "http://8.138.3.168:1888/h5/index?uuidNo=b457654bb15f4abd906985fd0ba2becf&signature=6b6d37d32a7a76ac72d014308ecd879b&nonce=VA9C52YlOcP1hE6VKEA1v5Vwjfq797RK&timestamp=1764033520715&merchantNo=M1763639259155",
    //             "mchUuidNo": "M1763639259155",
    //             "uuidNo": "b457654bb15f4abd906985fd0ba2becf"
    //         }
    // }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class HqjhClientResponse<T> {
        private int code;
        private String msg;
        private T data;

        @Data
        @AllArgsConstructor
        @NoArgsConstructor
        public static class GetUrlResponseData {
            private String url;
            private String mchUuidNo;
            private String uuidNo;
        }
    }
}
