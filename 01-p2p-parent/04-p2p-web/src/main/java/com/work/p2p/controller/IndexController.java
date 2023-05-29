package com.work.p2p.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.work.cons.Constants;
import com.work.p2p.beans.loan.LoanInfo;
import com.work.p2p.services.loan.BidInfoService;
import com.work.p2p.services.loan.LoanInfoService;
import com.work.p2p.services.user.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
public class IndexController {

    @Reference(interfaceClass = LoanInfoService.class,version = "1.0.0",check = false,timeout = 15000)
    private LoanInfoService loanInfoService;

    @Reference(interfaceClass = UserService.class,version = "1.0.0",check=false,timeout=15000)
    private UserService userService;

    @Reference(interfaceClass = BidInfoService.class,version = "1.0.0",check = false,timeout = 15000)
    private BidInfoService bidInfoService;



    @RequestMapping("/index")
    // 查询首页
    public String indexView(Model model){
        //一百个线程模拟高并发，检测provider的LoanInfoServiceImpl中redis读取情况以及双重验证和同步代码块
//        ExecutorService executorService = Executors.newFixedThreadPool(100);
//        for (int i = 0; i < 1000; i++) {
//            executorService.submit(new Runnable() {
//                @Override
//                public void run() {
//                    // 查询历史平均年化收益率
//                    Double historyAvgRate = loanInfoService.queryHistoryAvgRate();
//                    model.addAttribute(Constants.HISTORY_AVG_RATE, historyAvgRate);
//                }
//            });
//        }
//        // 关闭资源
//        executorService.shutdownNow();

        // 1. 查询历史平均年化收益率
       Double historyAvgRate = loanInfoService.queryHistoryAvgRate();
       model.addAttribute(Constants.HISTORY_AVG_RATE,historyAvgRate);
        // 2. 查询平台的用户数量
       Integer allUserCount =  userService.qurryAllUserCount();
       model.addAttribute(Constants.ALL_USER_COUNT,allUserCount);
        // 3. 查询平台的累计成交金额
        Double allBidMoney = bidInfoService.qurryAllBidMoney();
        model.addAttribute(Constants.ALL_BID_MONEY,allBidMoney);
        // 4. 查询新手宝产品

        //根据产品类型查询产品列表

        //新手宝的参数map
        Map<String,Object> params = new HashMap<>();
        params.put("productType",0);
        params.put("currentPage",0);
        params.put("pageSize",1);
        //查询新手宝列表
        List<LoanInfo> loanInfoListX = loanInfoService.qurryLoanInfoListByProductType(params);
        model.addAttribute(Constants.LOAN_INFO_LIST_X,loanInfoListX);

        // 5. 查询优选类产品
        params.put("productType",1);
        params.put("pageSize",4);
        List<LoanInfo> loanInfoListU = loanInfoService.qurryLoanInfoListByProductType(params);
        model.addAttribute(Constants.LOAN_INFO_LIST_U,loanInfoListU);
        // 6. 查询散标类产品
        params.put("productType",2);
        params.put("pageSize",8);
        List<LoanInfo> loanInfoListS = loanInfoService.qurryLoanInfoListByProductType(params);
        model.addAttribute(Constants.LOAN_INFO_LIST_S,loanInfoListS);


        return "index";
    }
}
