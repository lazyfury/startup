package io.sf.app.views.controller;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String CustomHandleError(HttpServletRequest request, HttpSession session, Model model){
        model.addAttribute("code",session.getAttribute("code"));
        model.addAttribute("message",session.getAttribute("message"));
        log.info("error handler" + session.getAttribute("message"));
        return "errorPage";
    }

}
