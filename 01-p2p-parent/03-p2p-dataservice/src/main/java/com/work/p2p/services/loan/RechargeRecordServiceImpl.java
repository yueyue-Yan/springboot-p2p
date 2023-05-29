package com.work.p2p.services.loan;

import com.alibaba.dubbo.config.annotation.Service;
import com.work.p2p.beans.loan.RechargeRecord;
import com.work.p2p.mapper.loan.RechargeRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * ClassName:RechargeRecordServiceImpl
 * Package:com.work.p2p.services.loan
 * Description: 充值Service的实现类
 *
 * @date:2023/4/29 22:13
 * @author:yueyue
 */
@Component
@Service(interfaceClass = RechargeRecordService.class,version = "1.0.0",timeout = 15000)
public class RechargeRecordServiceImpl implements RechargeRecordService {
    @Autowired
    private RechargeRecordMapper rechargeRecordMapper;
    /**生成充值记录*/
    @Override
    public int addRechargeRecord(RechargeRecord rechargeRecord) {
      return rechargeRecordMapper.insertSelective(rechargeRecord);
    }
}
