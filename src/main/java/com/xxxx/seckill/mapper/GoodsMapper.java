package com.xxxx.seckill.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xxxx.seckill.pojo.Goods;
import com.xxxx.seckill.vo.GoodsVo;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author Totoro
 * @since 2022-11-01
 */
public interface GoodsMapper extends BaseMapper<Goods> {
    /**
     * 获取商品列表i
     * @return
     */
    List<GoodsVo> findGoodsVo();

    /**
     * 获取商品详情
     *
     * @return
     */
    GoodsVo findGoodsVoByGoodsId(Long goodsId);
}
