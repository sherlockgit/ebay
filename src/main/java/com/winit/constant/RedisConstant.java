package com.winit.constant;

/**
 * redis常量
 * Created by liyou
 * 2017-07-30 16:22
 */
public interface RedisConstant {

    String TOKEN_PREFIX = "token_%s";

    Integer EXPIRE = 7200; //2小时
    Integer GOOD_CAR_EXPIRE = 86400; //24小时
}
