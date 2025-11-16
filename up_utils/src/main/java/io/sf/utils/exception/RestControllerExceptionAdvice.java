package io.sf.utils.exception;


import io.sf.utils.response.JsonResult;

import java.util.HashMap;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Component
@RestControllerAdvice
public class RestControllerExceptionAdvice {

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public JsonResult<String> handlerException(Exception exception)throws Exception{
        HashMap<String,Object> extra = new HashMap<>();
        extra.put("ExceptionHandler", "RestControllerExceptionAdvice");
        return new JsonResult<String>(HttpStatus.BAD_REQUEST.value(),"",exception.getMessage(),extra);
    }
}
