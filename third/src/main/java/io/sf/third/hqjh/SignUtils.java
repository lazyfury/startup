package io.sf.third.hqjh;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;


@Slf4j
public class SignUtils {

    private static final int ESTIMATED_PARAM_LENGTH = 16;

    // 定义需要排除的签名参数（不参与签名计算）
    private static final List<String> EXCLUDE_SIGN_KEYS = List.of(new String[]{"sign", "signature", "x-module-key", "search"});
    /**
     * 生成签名（基础版本）
     */
    public static String generateSign(Map<String, Object> params,
                                      Map<String, String> headers,
                                      String apiSecret) throws JsonProcessingException {
        if (apiSecret == null || apiSecret.isEmpty()) {
            throw new IllegalArgumentException("API Secret cannot be null or empty");
        }
        Map<String, Object> filteredParams = filterAndMergeParams(params, headers);
        log.info("filteredParams:{}", new ObjectMapper().writeValueAsString(filteredParams));
        String stringToSign = buildStringToSign(filteredParams);
        log.info("stringToSign:{}", stringToSign);
        return DigestUtils.md5Hex(stringToSign + apiSecret);
    }

    /**
     * 过滤并合并参数（增强版）
     * <p>
     * 修改点：
     * 1. 同时过滤 sign 和 signature 参数
     * 2. 保留headers中的apiKey/timestamp等关键参数
     */
    private static Map<String, Object> filterAndMergeParams(
            Map<String, Object> params, Map<String, String> headers) {
        Map<String, Object> mergedParams = new TreeMap<>();

        // 处理请求体参数
        if (params != null) {
            params.forEach((key, value) -> {
                if (ObjectUtils.isNotEmpty(value) && !EXCLUDE_SIGN_KEYS.contains(key)) {
                    mergedParams.put(key, value);
                }
            });
        }

        // 处理请求头参数（保留apiKey/timestamp/nonce等）
        if (headers != null) {
            headers.forEach((key, value) -> {
                if (ObjectUtils.isNotEmpty(value) && !EXCLUDE_SIGN_KEYS.contains(key)) {
                    mergedParams.put(key, value);
                }
            });
        }
        return mergedParams;
    }


    /**
     * 构建待签名字符串（类型安全版）
     */
    public static String buildStringToSign(Map<String, Object> params) {
        StringBuilder sb = new StringBuilder(params.size() * ESTIMATED_PARAM_LENGTH);
        params.forEach((key, value) -> {
            if(StringUtils.isBlank(value.toString())){
                return;
            }
            if (!sb.isEmpty()) {
                sb.append('&');
            }
            sb.append(key).append('=');

            if (value instanceof Collection || value.getClass().isArray()) {
                // 集合/数组类型转为JSON
                try {
                    sb.append(toJsonStr(value));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            } else if (value instanceof Map) {
                // Map类型转为JSON
                try {
                    sb.append(toJsonStr(value));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            } else {
                // 基础类型直接拼接
                sb.append(value);
            }
        });
        return sb.toString();
    }

    public static String toJsonStr(Object value)throws Exception{
        return new ObjectMapper().writeValueAsString(value);
    }
}