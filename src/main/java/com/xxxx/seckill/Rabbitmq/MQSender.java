package com.xxxx.seckill.Rabbitmq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Totoro
 * @create 05 18:52
 * @Description: 消息发送者
 */



@Slf4j
@Service
public class MQSender {






    @Autowired
    private RabbitTemplate rabbitTemplate;

//    public void send(Object msg){
//        log.info("消息发送"+msg);
//        rabbitTemplate.convertAndSend("queue",msg);
//    }
//
//    public void sendFanout(Object msg){
//        log.info("消息发送"+msg);
//        rabbitTemplate.convertAndSend("fanout_Exchange","",msg);
//    }
//
//
//    public void sendDirect01(Object msg){
//        log.info("发送red消息："+msg);
//        rabbitTemplate.convertAndSend("direct_Exchange","queue.red",msg);
//    }
//    public void sendDirect02(Object msg){
//        log.info("发送green消息："+msg);
//        rabbitTemplate.convertAndSend("direct_Exchange","queue.green",msg);
//    }




    public void sendSeckillMessage(String message){
        log.info("发送消息:"+message);
        rabbitTemplate.convertAndSend("seckillExchange","seckill.message",message);
    }
}
