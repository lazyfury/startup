package io.sf.app.api.hqjh.controller;


import io.sf.third.hqjh.HqjhGetUrlRequest;
import io.sf.utils.auth.AuthUtil;
import io.sf.utils.response.JsonResult;
import jakarta.servlet.http.HttpServletResponse;
import io.sf.third.hqjh.HqjhApiClient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;


@RestController
@RequestMapping("/api/hqjh")
public class HqjhController {
    @Autowired
    private HqjhApiClient client;


    @PostMapping("/getUrl")
    public JsonResult<HashMap<String, Object>> getUrl(@RequestBody HqjhGetUrlRequest hqjhGetUrlRequest, @AuthenticationPrincipal UserDetails userDetails) throws Exception {
        var resp = client.getUrl(AuthUtil.getUser().getId(), hqjhGetUrlRequest);
        var result = new HashMap<String, Object>();
        result.put("hqjh", resp);
        return new JsonResult<>(200, result, "ok");
    }

    @GetMapping("/jump")
    public void jump(@RequestParam String module, @RequestParam Long userId,HttpServletResponse response) throws Exception {
        var hqjhGetUrlRequest = new HqjhGetUrlRequest();
        
        hqjhGetUrlRequest.setPayUrl("https://distinct-instructor.biz/");
        hqjhGetUrlRequest.setExpireUrl("https://aggressive-custody.biz/");
        hqjhGetUrlRequest.setModuleKey(module);
        hqjhGetUrlRequest.setExchangeRate("1");
        hqjhGetUrlRequest.setUnitExchangeRate("1");
        hqjhGetUrlRequest.setUnit("元");
        hqjhGetUrlRequest.setAttach("non voluptate ut est cupidatat");
        hqjhGetUrlRequest.setClientType("h5");
        hqjhGetUrlRequest.setRoundType("0");
        hqjhGetUrlRequest.setChargeShowWay("0");
        hqjhGetUrlRequest.setUserCharge("0");
        
        var resp = client.getUrl(userId, hqjhGetUrlRequest);
        var url = resp.getData().getUrl();
        // return "redirect:/" + url;
        try {
            response.sendRedirect(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
