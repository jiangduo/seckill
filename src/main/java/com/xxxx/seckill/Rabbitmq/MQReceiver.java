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

    @RabbitListener(queues = "queue_fanout01")
    public void receive01(Object msg){
        log.info("QUEUE01接收消息："+msg);
    }

    @RabbitListener(queues = "queue_fanout02")
    public void receive02(Object msg){
        log.info("QUEUE02接收消息："+msg);
    }

    @RabbitListener(queues = "queue_direct01")
    public void receive03(Object msg){
        log.info("queue1接受消息："+msg);
    }


    @RabbitListener(queues = "queue_direct02")
    public void receive04(Object msg){
        log.info("queue02接收消息："+msg);
    }
}
