package io.sf.third.hqjh;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "hqjh")
public class HqjhProperties {
    private String apiKey;
    private String apiSecret;
    private String baseUrl;
    private String mchNo;

    public String getApiKey() { return apiKey; }
    public void setApiKey(String apiKey) { this.apiKey = apiKey; }
    public String getApiSecret() { return apiSecret; }
    public void setApiSecret(String apiSecret) { this.apiSecret = apiSecret; }
    public String getBaseUrl() { return baseUrl; }
    public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }
    public String getMchNo() { return mchNo; }
    public void setMchNo(String mchNo) { this.mchNo = mchNo; }
}