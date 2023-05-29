package com.work.p2p.services.user;

import com.alibaba.dubbo.config.annotation.Service;
import com.work.cons.Constants;
import com.work.p2p.beans.user.FinanceAccount;
import com.work.p2p.beans.user.User;
import com.work.p2p.mapper.user.FinanceAccountMapper;
import com.work.p2p.mapper.user.UserMapper;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Component
@Service(interfaceClass = UserService.class,version = "1.0.0",timeout=15000)
public class UserServiceImpl implements UserService {
    @Autowired
    UserMapper userMapper;
    @Autowired
    FinanceAccountMapper financeAccountMapper;
    @Autowired
    RedisTemplate<Object,Object> redisTemplate;

    @Override
    public Integer qurryAllUserCount() {
        Integer allUserCount = (Integer) redisTemplate.opsForValue().get(Constants.ALL_USER_COUNT);
        //双重验证+同步代码块
        if(!ObjectUtils.allNotNull(allUserCount)){
            synchronized (this){
                allUserCount = (Integer) redisTemplate.opsForValue().get(Constants.ALL_USER_COUNT);
                if(!ObjectUtils.allNotNull(allUserCount)){
                    allUserCount = userMapper.selectAllUserCount();
                    redisTemplate.opsForValue().set(Constants.ALL_USER_COUNT,allUserCount,7, TimeUnit.DAYS);
                }
            }
        }
        return allUserCount;
    }

    @Override
    public User queryUserByPhone(String phone) {
        return userMapper.selectUserByPhone(phone);
    }

    @Override
    //操作两张表，需要用到事务
    @Transactional
    public User register(String phone, String loginPassword) throws Exception {
        User user = new User();
        user.setPhone(phone);
        user.setLoginPassword(loginPassword);
        //user.setName();  // 真实姓名, 只有实名认证后才赋值
        //user.setIdCard(); 身份证号码，只有实名认证后才赋值
        user.setAddTime(new Date());
        user.setLastLoginTime(new Date());
        int userRows = userMapper.insertSelective(user);
        if (userRows == 0) {
            // 添加失败
            throw new Exception("注册用户，新增用户失败");
        }
        //给账户表的uid赋值，方法一userDetail，方法二，改造UserMapper.xml中的insertSelective方法：加入selectKey语句
        //User userDetail = userMapper.selectUserByPhone(phone);
        // 给新增的用户添加一个对应的账户,送888元红包
        FinanceAccount financeAccount = new FinanceAccount();
        //financeAccount.setUid(userDetail.getId());
        financeAccount.setUid(user.getId());
        financeAccount.setAvailableMoney(888.0);
        int faRows = financeAccountMapper.insertSelective(financeAccount);
        if (faRows == 0) {
            // 注册，新增账户失败
            throw new Exception("注册用户，新增账户失败");
        }

        return user;
    }
    //实名认证后，修改用户信息
    @Override
    public int modifyUser(User user) {
        return userMapper.updateByPrimaryKeySelective(user);
    }

    //用户登陆验证
    @Override
    public User queryUserByPhoneAndPwd(String phone, String loginPassword) {
        return userMapper.selectUserByPhoneAndPwd(phone,loginPassword);
    }


}

