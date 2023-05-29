package com.work.p2p.mapper.user;

import com.work.p2p.beans.user.FinanceAccount;

import java.util.Map;

public interface FinanceAccountMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(FinanceAccount record);

    int insertSelective(FinanceAccount record);

    FinanceAccount selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(FinanceAccount record);

    int updateByPrimaryKey(FinanceAccount record);

    /**根据uid查询账户信息*/
    FinanceAccount selectByUid(Integer uid);
    /**投资后更新(减少)u_finance_account表的对应账户的余额available_money*/
    int updateByBidMoney(Map<String, Object> params);
    /**根据uid bidMoney incomeMoney 返回收益给相关用户的账户*/
    int updateFinanceAccountByIncomeBack(Map<String, Object> params);
}