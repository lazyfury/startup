package io.sf.app.api.hqjh.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import io.sf.third.hqjh.HqjhGetUrlRequest;
import io.sf.utils.auth.AuthUtil;
import io.sf.utils.response.JsonResult;
import io.sf.modules.auth.security.CustomUserDetail;
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
    public JsonResult<HashMap<String, Object>> getUrl(@RequestBody HqjhGetUrlRequest hqjhGetUrlRequest, @AuthenticationPrincipal UserDetails userDetails) throws JsonProcessingException, InterruptedException {
        var resp = client.getUrl(AuthUtil.getUser().getId(), hqjhGetUrlRequest);
        var result = new HashMap<String, Object>();
        result.put("hqjh", resp);
        return new JsonResult<>(200, result, "ok");
    }
}
