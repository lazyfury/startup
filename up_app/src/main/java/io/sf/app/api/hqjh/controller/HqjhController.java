package io.sf.app.api.hqjh.controller;


import io.sf.third.hqjh.HqjhGetUrlRequest;
import io.sf.utils.auth.AuthUtil;
import io.sf.utils.response.JsonResult;
import jakarta.servlet.http.HttpServletResponse;
import io.sf.third.hqjh.HqjhApiClient;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Optional;


@Slf4j
@RestController
@RequestMapping("/api/hqjh")
public class HqjhController {
    @Autowired
    private HqjhApiClient client;

    @Autowired
    private HqjhGetUrlRequest _hqjhGetUrlRequest;



    @PostMapping("/getUrl")
    public JsonResult<HashMap<String, Object>> getUrl(@RequestBody HqjhGetUrlRequest hqjhGetUrlRequest, @AuthenticationPrincipal UserDetails userDetails) throws Exception {
        var resp = client.getUrl(AuthUtil.getUser().getId(), hqjhGetUrlRequest);
        var result = new HashMap<String, Object>();
        result.put("hqjh", resp);
        return new JsonResult<>(200, result, "ok");
    }

    @GetMapping("/jump")
    public void jump(@RequestParam Optional<String> module, @RequestParam Long userId,@RequestParam Optional<String> showWay,@RequestParam Optional<String> rate,@RequestParam Optional<String> userCharge,HttpServletResponse response) throws Exception {
        var hqjhGetUrlRequest = new HqjhGetUrlRequest();
        
        hqjhGetUrlRequest.setModuleKey(module.orElse(""));
        hqjhGetUrlRequest.setExchangeRate(rate.orElse("1"));
        hqjhGetUrlRequest.setUnitExchangeRate(rate.orElse("1"));
        hqjhGetUrlRequest.setChargeShowWay(showWay.orElse("0"));
        hqjhGetUrlRequest.setUserCharge(userCharge.orElse("0"));

        hqjhGetUrlRequest.fill(_hqjhGetUrlRequest);
        
        var resp = client.getUrl(userId, hqjhGetUrlRequest);
        var url = resp.getData().getUrl();
        // return "redirect:/" + url;
        try {
            response.sendRedirect(url);
        } catch (Exception e) {
            log.error(e.toString());
        }
    }

}
