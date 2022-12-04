package com.xxxx.seckill.exception;


import com.xxxx.seckill.vo.RespBean;
import com.xxxx.seckill.vo.RespBeanEnum;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;



/**
 *  全局异常处理类
 * @author Totoro
 * @create 29 14:44
 * @Description:
 */

//这样返回的就是responsebody，不用在方法上具体返回
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)//里面是处理的异常类
    public RespBean ExceptionHandler(Exception e){
        if (e instanceof GlobalException) {   //Glbalextion要继承runtimeExceptin，不要这里报错
            GlobalException exception = (GlobalException) e;
            return RespBean.error(exception.getRespBeanEnum());
        }else if (e instanceof BindException){//判断绑定异常
            BindException ex = (BindException) e;
            RespBean respBean = RespBean.error(RespBeanEnum.BIND_ERROR);
            //但是这样只能返回绑定异常（BIND_ERROR）的信息，就是”参数校验异常“，要把绑定异常跑出来的信息拿出来,如isMobile中的”手机号码格式错误
            respBean.setMessage("参数校验异常："+ ex.getBindingResult().getAllErrors().get(0).getDefaultMessage());
            return respBean;
        }
        return RespBean.error(RespBeanEnum.ERROR);
    }
}
