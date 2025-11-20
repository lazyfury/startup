package io.sf.third.hqjh;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HqjhGetUrlRequest{
    private String serviceUserKey;
    private String payUrl = "http://localhost:8080/hqjh/pay";
    private String expireUrl = "http://localhost:8080/hqjh/expire";
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
}