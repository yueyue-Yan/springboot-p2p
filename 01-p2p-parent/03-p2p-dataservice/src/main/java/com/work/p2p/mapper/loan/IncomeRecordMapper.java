package com.work.p2p.mapper.loan;

import com.work.p2p.beans.loan.IncomeRecord;

import java.util.List;

public interface IncomeRecordMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(IncomeRecord record);

    int insertSelective(IncomeRecord record);

    IncomeRecord selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(IncomeRecord record);

    int updateByPrimaryKey(IncomeRecord record);
    //根据当前时间和收益状态返回收益记录列表
    List<IncomeRecord> selectIncomeRecordListByCurDateAndIncomeStatus(int incomeStatus);
}