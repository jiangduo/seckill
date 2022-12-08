package com.xxxx.seckill.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Totoro
 * @create 08 15:34
 * @Description: 秒杀信息
 *          遇见库存后要下单，其中需要传入用户和商品
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeckillMessage {

    private User user;
    private Long goodId;
}
