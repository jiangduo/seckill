package com.xxxx.seckill.controller;

import com.xxxx.seckill.pojo.User;
import com.xxxx.seckill.service.IGoodsService;
import com.xxxx.seckill.service.IUserService;
import com.xxxx.seckill.vo.DetailVo;
import com.xxxx.seckill.vo.GoodsVo;
import com.xxxx.seckill.vo.RespBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author Totoro
 * @create 29 18:24
 * @Description:
 */

@Slf4j
@Controller
@RequestMapping("/goods")
public class GoodsController {

    @Autowired
    private IUserService userService;
    @Autowired
    private IGoodsService goodsService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private ThymeleafViewResolver thymeleafViewResolver;

    /**
     * 跳转商品列表页
     * windows优化前 OPS：1869
     * linux优化前 OPS：470
     * windows缓存后 OPS：2968
     * @param session
     * @param model
     * @param ticket
     * @return 优化：不单纯页面跳转，返回整个页面，并且页面要缓存起来
     */
    @RequestMapping(value = "/toList", produces = "text/html;charset=utf-8")//因为不单单是页面跳转，要返回完整页面并且页面要缓存起来，
    @ResponseBody
    public String toList(Model model, User user, HttpServletRequest request, HttpServletResponse response) {
//        if(StringUtils.isEmpty(ticket)){
//            return "login";
//        }
//        //User user = (User)session.getAttribute(ticket);
//        User user = userService.getUserByCookie(ticket, request, response);
//
//        if (null == user) {
//            return "login";
//        }
        //思考将商品列表页作为页面缓存起来需要哪些操作
        //1、从redis中读取缓存,有页面直接返回给浏览器，
        ValueOperations valueOperations = redisTemplate.opsForValue();
        String html = (String) valueOperations.get("goodsList");//返回String，因为这里确定返回的是一个html页面，必然是一个String类型，
        if (!StringUtils.isEmpty(html)) {
            return html;
        }
        model.addAttribute("user", user);
        model.addAttribute("goodsList", goodsService.findGoodsVo());
        //return "goodsList";
        //2、如果没有yemian1手动渲染模板并存储到redis中，以及将结果输出到浏览器端
        WebContext webContext = new WebContext(request, response, request.getServletContext(), request.getLocale(), model.asMap());//点击方法可以看到方法所需的参数,最后一个参数是map，是放在thymeleaf中的数据，此前是modelandview添加吧key和value添加进去，这里渲染就还是model，但是这里要求map就转成map
        html = thymeleafViewResolver.getTemplateEngine().process("goodsList", webContext);//获取thyme引擎然后渲染

        if (!StringUtils.isEmpty(html)) {
            valueOperations.set("goodsList", html, 60, TimeUnit.SECONDS);//页面放到缓存，如果永不失效数据都是写死的，,这里设置1分钟失效
        }
        return html;
    }

    /**
     * 跳转商品详情页（旧）
     *
     * @param goodsId
     * @return
     */
    @RequestMapping(value = "/toDetail2/{goodsId}",produces = "text/html;charset=utf-8")
    @ResponseBody
    public String toDetail2(Model model, User user, @PathVariable Long goodsId, HttpServletRequest request, HttpServletResponse response) {
        //解释同上
        ValueOperations valueOperations = redisTemplate.opsForValue();
        String html = (String) valueOperations.get("goodsDetail:" + goodsId);//现在不同goodid进入不同页面，缓存起来
        if (!StringUtils.isEmpty(html)) {
            return html;
        }
        model.addAttribute("user", user);
        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodsId);
//        log.info("测试开始{}",goodsVo);
        Date startDate = goodsVo.getStartDate();
        Date endDate = goodsVo.getEndDate();
//        Date endDate = goodsVo.getEndDate();
        Date nowDate = new Date();
        //秒杀状态
        int secKillStatus = 0;
        //秒杀倒计时
        int remainSeconds = 0;
        //秒杀未开始
        if (nowDate.before(startDate)) {
            //距离结束时间,获取的是毫秒，除以1000得到秒数
            remainSeconds = (int) ((startDate.getTime() - nowDate.getTime()) / 1000);

        } else if (nowDate.after(endDate)) {
            //秒杀已经结束
            secKillStatus = 2;
            remainSeconds = -1;
        } else {
            secKillStatus = 1;
            remainSeconds = 0;
        }
        model.addAttribute("remainSeconds", remainSeconds);
        model.addAttribute("secKillStatus", secKillStatus);
        model.addAttribute("goods", goodsVo);
        WebContext webContext = new WebContext(request, response, request.getServletContext(), request.getLocale(), model.asMap());
        html = thymeleafViewResolver.getTemplateEngine().process("goodsDetail", webContext);
        if (!StringUtils.isEmpty(html)) {
            valueOperations.set("goodsDetail:" + goodsId, html, 60, TimeUnit.SECONDS);
        }
//        return "goodsDetail";
        return html;
    }


    /**
     * 跳转商品详情页（新）
     *
     * @param goodsId
     * @return
     */
    @RequestMapping("/detail/{goodsId}")
    @ResponseBody
    public RespBean toDetail(Model model, User user, @PathVariable Long goodsId) {


        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodsId);
//        log.info("测试开始{}",goodsVo);
        Date startDate = goodsVo.getStartDate();
        Date endDate = goodsVo.getEndDate();
//        Date endDate = goodsVo.getEndDate();
        Date nowDate = new Date();
        //秒杀状态
        int secKillStatus = 0;
        //秒杀倒计时
        int remainSeconds = 0;
        //秒杀未开始
        if (nowDate.before(startDate)) {
            //距离结束时间,获取的是毫秒，除以1000得到秒数
            remainSeconds = (int) ((startDate.getTime() - nowDate.getTime()) / 1000);

        } else if (nowDate.after(endDate)) {
            //秒杀已经结束
            secKillStatus = 2;
            remainSeconds = -1;
        } else {
            secKillStatus = 1;
            remainSeconds = 0;
        }
        //处理返回逻辑
        DetailVo detailVo = new DetailVo();
        detailVo.setUser(user);
        detailVo.setGoodsVo(goodsVo);
        detailVo.setSecKillStatus(secKillStatus);
        detailVo.setRemainSeconds(remainSeconds);

        return RespBean.success(detailVo);
    }






}
