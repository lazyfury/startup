package io.sf.app.views.advice.exception;


import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Slf4j
@Component
@ControllerAdvice(basePackages = "io.sf.app.views")
public class MvcControllerExceptionAdvice {

    @ExceptionHandler(Exception.class)
    public String exHandler(Exception exception, HttpSession session, RedirectAttributes redirectAttributes){
        log.info("自定义错误处理器:======"+ exception.getMessage() + "===========");
        redirectAttributes.addFlashAttribute("code",400);
        redirectAttributes.addFlashAttribute("message",exception.getMessage());
        return "redirect:/error";
    }
}
