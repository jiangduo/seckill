package com.xxxx.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xxxx.seckill.pojo.SeckillOrder;
import com.xxxx.seckill.pojo.User;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Totoro
 * @since 2022-11-01
 */
public interface ISeckillOrderService extends IService<SeckillOrder> {
    /**
     *  获取秒杀结果
     * @param user
     * @param goodsId
     * @return  orderId:成功 -1;秒杀失败。0：排队中
     */
    Long getResult(User user, Long goodsId);
}
