package com.work.timer;

import com.alibaba.dubbo.config.annotation.Reference;
import com.work.p2p.services.loan.IncomeRecordService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * ClassName:TimerManger
 * Package:com.work.timer
 * Description: 定时任务类
 * 本质上类似04-web工程的controller，只不过controller由操作页面触发其内的方法，而timerManager通过cron表达式自动调用方法
 *
 * @date:2023/4/28 19:53
 * @author:yueyue
 */
@Component
public class TimerManger {

    @Reference(interfaceClass = IncomeRecordService.class, version = "1.0.0",check = false, timeout = 15000)
     IncomeRecordService incomeRecordService;

    /**生成收益计划*/
    @Scheduled(cron = "0/5 * * * * *")
    public void generateIncomePlan() throws Exception {
        System.out.println("=====生成收益计划开始=====");
        incomeRecordService.generateIncomePlan();
        System.out.println("=====生成收益计划结束=====");
    }


    /**返还收益*/
    @Scheduled(cron = "0/5 * * * * *")
    public void generateIncomeBack() throws Exception{
        System.out.println("=========返还收益开始===========");
        incomeRecordService.generateIncomeBack();
        System.out.println("=========返还收益结束===========");
    }
}
