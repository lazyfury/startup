package io.sf.app.api.hqjh.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import io.sf.utils.response.JsonResult;
import io.sf.third.hqjh.HqjhApiClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@RequestMapping("/api/hqjh")
public class HqjhController {
    @Autowired
    private HqjhApiClient client;
    @GetMapping("/getUrl")
    public JsonResult getUrl(@RequestParam String moduleKey) throws JsonProcessingException {
        var resp = client.callApi(9527L,moduleKey);
        var result = new HashMap<String,Object>();
        result.put("hqjh",resp);
        return new JsonResult(200,result,"ok");
    }
}
