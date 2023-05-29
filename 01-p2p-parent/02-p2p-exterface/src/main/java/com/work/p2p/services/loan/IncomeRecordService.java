package com.work.p2p.services.loan;

/**
 * ClassName:IncomeRecordService
 * Package:com.work.p2p.services.loan
 * Description: 收益计划Service
 *  *
 * @date:2023/4/28 20:09
 * @author:yueyue
 */
public interface IncomeRecordService {

    void generateIncomePlan() throws Exception;

    void generateIncomeBack() throws Exception;
}
