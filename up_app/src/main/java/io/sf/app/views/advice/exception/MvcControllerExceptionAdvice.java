package io.sf.app.views.advice.exception;


import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


@Slf4j
@Component
@ControllerAdvice(basePackages = "io.sf.app.views")
public class MvcControllerExceptionAdvice {

    @ExceptionHandler(Exception.class)
    public String exHandler(Exception exception, HttpSession session){
        log.info("自定义错误处理器:======"+ exception.getMessage() + "===========");
        session.setAttribute("code",400);
        session.setAttribute("message",exception.getMessage());
        return "redirect:/error";
    }
}
