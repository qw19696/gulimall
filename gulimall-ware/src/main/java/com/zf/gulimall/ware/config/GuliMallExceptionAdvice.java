package com.zf.gulimall.ware.config;

import com.zf.common.enums.BizCodeEnums;
import com.zf.common.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice(basePackages = "com.zf.gulimall.ware.controller")
public class GuliMallExceptionAdvice {

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public R handleValidException(MethodArgumentNotValidException e){
        BindingResult bindingResult = e.getBindingResult();
        Map<String,String> res = new HashMap<>();
        bindingResult.getFieldErrors().forEach(err ->{
            res.put(err.getField(), err.getDefaultMessage());
        });
        log.error("数据校验错误:{}",res);
        return R.error(BizCodeEnums.VALID_EXCEPTION.getCode(), res.toString());
    }

    @ExceptionHandler(value = Throwable.class)
    public R allExceptionRes(Throwable t){
        log.error(t.getMessage(), t);
        return R.error(BizCodeEnums.UNKNOW_EXCEPTION.getCode(), BizCodeEnums.UNKNOW_EXCEPTION.getMsg());
    }

    @ExceptionHandler(value = RuntimeException.class)
    public R runtimeExceptionRes(RuntimeException t){
        log.error(t.getMessage(), t);
        return R.error(BizCodeEnums.UNKNOW_EXCEPTION.getCode(), t.getMessage());
    }
}
