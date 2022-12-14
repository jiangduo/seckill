package com.xxxx.seckill.controller;


import com.xxxx.seckill.Rabbitmq.MQSender;
import com.xxxx.seckill.pojo.User;
import com.xxxx.seckill.vo.RespBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author Totoro
 * @since 2022-10-24
 */
@Controller
@RequestMapping("/user")
public class UserController {


    @Autowired
    private MQSender mqSender;
/**
 * 用户信息（测试）
 */
    @RequestMapping("/info")
    @ResponseBody
    public RespBean info(User user){
       return RespBean.success(user);
    }



//    @RequestMapping("/mq")
//    @ResponseBody
//    public void mq(){
//        mqSender.send("hello");
//    }
//    @RequestMapping("/mq/fanout")
//    @ResponseBody
//    public void mqFanout(){
//        mqSender.sendFanout("hello,fanout");
//    }
//
//    @RequestMapping("/mq/red")
//    @ResponseBody
//    public void mq01(){
//        mqSender.sendDirect01("hello,red");
//    }
//    @RequestMapping("/mq/green")
//    @ResponseBody
//    public void mq02(){
//        mqSender.sendDirect02("hello,green");
//    }
}
