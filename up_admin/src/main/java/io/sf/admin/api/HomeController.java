package io.sf.admin.api;


import io.sf.utils.response.JsonResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("")
    public JsonResult home(){
        return new JsonResult(200,null,"welcome!");
    }
}
