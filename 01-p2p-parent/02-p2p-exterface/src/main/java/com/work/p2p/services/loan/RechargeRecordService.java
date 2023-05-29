package com.work.p2p.services.loan;

import com.work.p2p.beans.loan.RechargeRecord;

/**
 * ClassName:RechargeRecordService
 * Package:com.work.p2p.services.loan
 * Description: 充值Service
 *
 * @date:2023/4/29 22:12
 * @author:yueyue
 */
public interface RechargeRecordService {
    /**生成充值记录*/
    int addRechargeRecord(RechargeRecord rechargeRecord);
}
