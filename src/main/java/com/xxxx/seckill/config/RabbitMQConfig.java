package com.xxxx.seckill.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Totoro
 * @create 05 18:50
 * @Description: 配置队列
 */
@Configuration
public class RabbitMQConfig {

    @Bean
    public Queue queue(){
        return new Queue("queue",true);
    }

}
