package com.xxxx.seckill.Rabbitmq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Totoro
 * @create 05 18:55
 * @Description: 消息消费者
 */

@Service
@Slf4j
public class MQReceiver {



    @RabbitListener(queues = "queue")
    public void receive(Object msg){
        log.info("接受消息："+msg);

    }
}
