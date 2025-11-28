package io.sf.third.hqjh;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Component
@ConfigurationProperties(prefix = "hqjh-jump-params")
public class HqjhGetUrlRequest{
    private String serviceUserKey;
    private String payUrl;
    private String expireUrl;
    private String mchUuidNo;
    private String moduleKey;
    private String exchangeRate;
    private String unitExchangeRate;
    private String unit;
    private String attach;
    private String clientType;
    private String roundType;
    private Object params;
    private String chargeShowWay;
    private String userCharge;

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("serviceUserKey", serviceUserKey);
        map.put("payUrl", payUrl);
        map.put("expireUrl", expireUrl);
        map.put("mchUuidNo", mchUuidNo);
        map.put("moduleKey", moduleKey);
        map.put("exchangeRate", exchangeRate);
        map.put("unitExchangeRate", unitExchangeRate);
        map.put("unit", unit);
        map.put("attach", attach);
        map.put("clientType", clientType);
        map.put("roundType", roundType);
        map.put("params", params);
        map.put("chargeShowWay", chargeShowWay);
        map.put("userCharge", userCharge);
        return map;
    }

    public void fill(HqjhGetUrlRequest hqjhGetUrlRequest) {
        this.serviceUserKey = Optional.ofNullable(this.serviceUserKey).orElse(hqjhGetUrlRequest.getServiceUserKey());
        this.payUrl = Optional.ofNullable(this.payUrl).orElse(hqjhGetUrlRequest.getPayUrl());
        this.expireUrl = Optional.ofNullable(this.expireUrl).orElse(hqjhGetUrlRequest.getExpireUrl());
        this.mchUuidNo = Optional.ofNullable(this.mchUuidNo).orElse(hqjhGetUrlRequest.getMchUuidNo());
        this.moduleKey = Optional.ofNullable(this.moduleKey).orElse(hqjhGetUrlRequest.getModuleKey());
        this.exchangeRate = Optional.ofNullable(this.exchangeRate).orElse(hqjhGetUrlRequest.getExchangeRate());
        this.unitExchangeRate = Optional.ofNullable(this.unitExchangeRate).orElse(hqjhGetUrlRequest.getUnitExchangeRate());
        this.unit = Optional.ofNullable(this.unit).orElse(hqjhGetUrlRequest.getUnit());
        this.attach = Optional.ofNullable(this.attach).orElse(hqjhGetUrlRequest.getAttach());
        this.clientType = Optional.ofNullable(this.clientType).orElse(hqjhGetUrlRequest.getClientType());
        this.roundType = Optional.ofNullable(this.roundType).orElse(hqjhGetUrlRequest.getRoundType());
        this.params = Optional.ofNullable(this.params).orElse(hqjhGetUrlRequest.getParams());
        this.chargeShowWay = Optional.ofNullable(this.chargeShowWay).orElse(hqjhGetUrlRequest.getChargeShowWay());
        this.userCharge = Optional.ofNullable(this.userCharge).orElse(hqjhGetUrlRequest.getUserCharge());
    }
}