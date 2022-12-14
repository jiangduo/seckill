package com.xxxx.seckill.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xxxx.seckill.Rabbitmq.MQSender;
import com.xxxx.seckill.pojo.Order;
import com.xxxx.seckill.pojo.SeckillMessage;
import com.xxxx.seckill.pojo.SeckillOrder;
import com.xxxx.seckill.pojo.User;
import com.xxxx.seckill.service.IGoodsService;
import com.xxxx.seckill.service.IOrderService;
import com.xxxx.seckill.service.ISeckillOrderService;
import com.xxxx.seckill.utils.JsonUtil;
import com.xxxx.seckill.vo.GoodsVo;
import com.xxxx.seckill.vo.RespBean;
import com.xxxx.seckill.vo.RespBeanEnum;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Totoro
 * @create 02 14:39
 * @Description: 秒杀功能
 */
@Controller
@RequestMapping("/seckill")
public class SecKillController implements InitializingBean {

    @Autowired
    private IGoodsService goodsService;
    @Autowired
    private ISeckillOrderService seckillOrderService;
    @Autowired
    private IOrderService orderService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private MQSender mqSender;
    @Autowired
    private RedisScript<Long> script;

    private Map<Long,Boolean> EmptyStockMap = new HashMap<>();
    /**
     * .
     * <p>
     * 秒杀功能(旧)
     * <p>
     * window 优化前  1780
     * linux 优化前 362
     *
     * @param model
     * @param user
     * @param goodsId
     * @return
     */
    @RequestMapping("/doSeckill")
    public String doSeckill2(Model model, User user, Long goodsId) {
        if (null == user) {
            return "login";
        }
        model.addAttribute("user", user);
        //根据商品ID再查询库存够不够，不能根据前端传过来的库存判断
        GoodsVo goods = goodsService.findGoodsVoByGoodsId(goodsId);
        //判断库存
        if (goods.getStockCount() < 1) {
            //库存不足就传入错误信息，并跳转错误页面
            model.addAttribute("errmsg", RespBeanEnum.EMPTY_STOCK.getMessage());
            return "secKillFail";
        }
        //判断订单是否重复
        SeckillOrder secKillOrder =
                seckillOrderService.getOne(new QueryWrapper<SeckillOrder>().eq("user_id", user.getId()).eq("goods_id", goodsId));
        if (secKillOrder != null) {
            model.addAttribute("errmsg", RespBeanEnum.REPEATE_ERROR.getMessage());
            return "secKillFail";
        }
        Order order = orderService.secKill(user, goods);
        //将订单和订单中的商品传入
        model.addAttribute("order", order);
        model.addAttribute("goods", goods);
        return "orderDetail";
    }

    /**
     * .
     * <p>
     * 秒杀功能(新)
     * <p>
     * window 优化前  1780
     * linux 优化前 362
     * <p>
     * window 优化后 2475
     *
     * @param model
     * @param user
     * @param goodsId
     * @return
     */
    @RequestMapping(value = "/doSeckill", method = RequestMethod.POST)
    @ResponseBody
    public RespBean doSeckill(Model model, User user, Long goodsId) {
        if (null == user) {
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }

        //redis预减库存
        ValueOperations valueOperations = redisTemplate.opsForValue();
        //判断是否重复抢购（不变）
        SeckillOrder seckillOrder =
                (SeckillOrder) redisTemplate.opsForValue().get("order:" + user.getId() + ":" + goodsId);
        if (seckillOrder != null) {

            return RespBean.error(RespBeanEnum.REPEATE_ERROR);
        }
        //内存标记减少redis访问
        if(EmptyStockMap.get(goodsId)){
            return  RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }
        //二、获取到递减之后的库存并判断redis
//        Long stock = valueOperations.decrement("seckillGoods" + goodsId);//递减：调用一次减一 且原子性(不会被线程打断？)，，返回结果是递减之后的库存，
        Long stock = (Long) redisTemplate.execute(script, Collections.singletonList("seckillGoods" + goodsId), Collections.EMPTY_LIST);
        if (stock<0) {
            EmptyStockMap.put(goodsId,true);
            //上面调用一次递减1，到这里是-1，在加一次。
            valueOperations.increment("seckillGoods"+goodsId);
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }

        //三、预减库存后 下单
//        Order order = orderService.secKill(user,goods);
        SeckillMessage seckillMessage = new SeckillMessage(user, goodsId);
        mqSender.sendSeckillMessage(JsonUtil.object2JsonStr(seckillMessage));//这是一个对象需要转一下,mq传对象必须要序列化，，转json后不用，
        return RespBean.success(0);

        /*
        //根据商品ID再查询库存够不够，不能根据前端传过来的库存判断
        GoodsVo goods = goodsService.findGoodsVoByGoodsId(goodsId);
        //判断库存
        if (goods.getStockCount() < 1) {
            //库存不足就传入错误信息，并跳转错误页面
//            model.addAttribute("errmsg", RespBeanEnum.EMPTY_STOCK.getMessage());
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }
        //判断订单是否重复(旧)现在直接从redis中获取
//        SeckillOrder secKillOrder =
//                seckillOrderService.getOne(new QueryWrapper<SeckillOrder>().eq("user_id", user.getId()).eq("goods_id", goodsId));
        SeckillOrder seckillOrder = (SeckillOrder) redisTemplate.opsForValue().get("order:" + user.getId() + ":" + goodsId);
        if (seckillOrder!=null) {
//            model.addAttribute("errmsg",RespBeanEnum.REPEATE_ERROR.getMessage());
            return RespBean.error(RespBeanEnum.REPEATE_ERROR);
        }
        Order order = orderService.secKill(user,goods);

        return RespBean.success(order);

         */

//        return null;
    }


    /**
     *   获取秒杀结果
     * @param user
     * @param goodsId
     * @return orderId:成功 -1;秒杀失败。0：排队中
     */
    @RequestMapping(value = "/result",method = RequestMethod.GET)
    @ResponseBody
    public RespBean getResult(User user,Long goodsId){
        if(user ==null){
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        Long orderId = seckillOrderService.getResult(user,goodsId);
        return RespBean.success(orderId);
    }












    /**
     * 一、系统初始化时，执行的方法，将商品库存加载redis中，
     *
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        List<GoodsVo> list = goodsService.findGoodsVo();
        //判断有没有库存
        if (CollectionUtils.isEmpty(list)) {
            return;
        }

        //不为空就存到redis中
        list.forEach(goodsVo -> {
            redisTemplate.opsForValue().set("seckillGoods" + goodsVo.getId(), goodsVo.getStockCount());
            EmptyStockMap.put(goodsVo.getId(),false);
        });
    }
}
