package com.work.p2p.services.user;

import com.work.p2p.beans.user.FinanceAccount;

/**
 * ClassName:FinanceAccountService
 * Package:com.work.p2p.services.user
 * Description: 账户service
 *
 * @date:2023/4/26 20:17
 * @author:yueyue
 */
public interface FinanceAccountService {
    /**根据用户id获取账户余额*/
    FinanceAccount queryFinanceAccountByUid(Integer uid);
}

