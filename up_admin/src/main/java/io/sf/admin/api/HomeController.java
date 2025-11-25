package io.sf.admin.api;


import io.sf.utils.response.JsonResult;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "首页")
public class HomeController {

    @GetMapping("")
    public JsonResult<Object> home(){
        return new JsonResult<>(200,null,"welcome!");
    }
}
