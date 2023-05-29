package com.work.p2p.services.user;

import com.alibaba.dubbo.config.annotation.Service;
import com.work.p2p.beans.user.FinanceAccount;
import com.work.p2p.mapper.user.FinanceAccountMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * ClassName:FinanceAccountServiceImpl
 * Package:com.work.p2p.services.user
 * Description: 描述信息
 *
 * @date:2023/4/26 20:19
 * @author:yueyue
 */
@Component
@Service(interfaceClass = FinanceAccountService.class,version = "1.0.0",timeout = 15000)
public class FinanceAccountServiceImpl implements FinanceAccountService {
    @Autowired
    FinanceAccountMapper financeAccountMapper;

    @Override
    public FinanceAccount queryFinanceAccountByUid(Integer uid) {
        return financeAccountMapper.selectByUid(uid);
    }
}
