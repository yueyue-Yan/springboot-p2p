package com.work.p2p.services.user;

import com.alibaba.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * ClassName:RedisServiceImpl
 * Package:com.work.p2p.services.user
 * Description: 操作redis的service的实现
 *
 * @date:2023/4/24 16:44
 * @author:yueyue
 */
@Component
@Service(interfaceClass = RedisService.class,version = "1.0.0",timeout = 15000)
public class RedisServiceImpl implements RedisService {
    @Autowired
    private RedisTemplate<Object,Object> redisTemplate;
    /**添加手机号和验证码数据到redis中，设置超时时间是30min*/
    @Override
    public void put(String key, String value) {
        redisTemplate.opsForValue().set(key,value,30, TimeUnit.MINUTES);
    }

    @Override
    public String get(String key) {
        return (String) redisTemplate.opsForValue().get(key);
    }
}
