package io.sf.utils.response;

import java.util.Map;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class JsonResult<T extends Object> {
    private int code;
    private T data;
    private String message;
    private Map<String,Object> extra;

    public JsonResult(int code, T data, String message){
        this.code = code;
        this.data = data;
        this.message = message;
    }

    public JsonResult(int code, T data, String message, Map<String,Object> extra){
        this.code = code;
        this.data = data;
        this.message = message;
        this.extra = extra;
    }
}
