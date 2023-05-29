package com.work.p2p.services.user;

/**
 * ClassName:RedisService
 * Package:com.work.p2p.services.user
 * Description: 操作redis的service
 *
 * @date:2023/4/24 16:41
 * @author:yueyue
 */
public interface RedisService {
    /**向redis中保存对应的验证码数据*/
    void put(String key, String value);
    /**在redis数据库中根据key(手机号)获取暂存的value(验证码)*/
    String get(String key);
}
