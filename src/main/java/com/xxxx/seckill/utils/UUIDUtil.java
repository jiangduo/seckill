package com.xxxx.seckill.utils;

import java.util.UUID;

/**
 * UUID工具类
 *
 * @author zhoubin
 * @since 1.0.0
 */
public class UUIDUtil {
    public static String uuid() {

        return UUID.randomUUID().toString().replace("-", "");//生成uuid然后将其中短杠去除掉，
    }
}