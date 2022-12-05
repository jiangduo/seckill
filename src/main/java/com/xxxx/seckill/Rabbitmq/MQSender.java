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

    public void send(Object msg){
        log.info("消息发送"+msg);
        rabbitTemplate.convertAndSend("queue",msg);
    }
}
