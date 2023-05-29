package com.work.p2p.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.work.cons.Constants;
import com.work.p2p.beans.loan.RechargeRecord;
import com.work.p2p.beans.user.User;
import com.work.p2p.services.loan.KuaiQianService;
import com.work.p2p.services.loan.RechargeRecordService;
import com.work.utils.DateUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Map;

/**
 * ClassName:RechargeRecordController
 * Package:com.work.p2p.controller
 * Description: 充值相关的controller
 *
 * @date:2023/4/29 13:09
 * @author:yueyue
 */
@Controller
public class RechargeRecordController {

    @Reference(interfaceClass = KuaiQianService.class,version = "1.0.0",check = false,timeout = 15000)
    KuaiQianService kuaiQianService;

    @Reference(interfaceClass = RechargeRecordService.class, version = "1.0.0", check = false, timeout = 15000)
    RechargeRecordService rechargeRecordService;

    /**跳转到充值界面*/
    @RequestMapping("/loan/page/toRecharge")
    public String ToRecharge(){
        return "/toRecharge";
    }
    /**发起支付请求*/
    @RequestMapping("/loan/toRecharge")
    public String submitRecharge(HttpServletRequest request,
                                 Model model,
                                 @RequestParam("rechargeMoney") String yuan
                                ){
        //通过session获取用户信息
        User user = (User) request.getSession().getAttribute(Constants.SESSION_USER);

        //充值记录rechargeRecord
        RechargeRecord rechargeRecord = new RechargeRecord();

        rechargeRecord.setUid(user.getId());
        rechargeRecord.setRechargeStatus("0");  // 0:充值中 1：充值成功 2: 充值失败
        rechargeRecord.setRechargeMoney(Double.parseDouble(yuan) * 100);
        rechargeRecord.setRechargeTime(new Date());
        rechargeRecord.setRechargeDesc("快钱充值");

        String rechargeNo = DateUtil.generateOrderId();
        rechargeRecord.setRechargeNo(rechargeNo);

        // 添加充值记录
        int rows = rechargeRecordService.addRechargeRecord(rechargeRecord);
        if (rows == 0) {
            model.addAttribute("target_msg","新增充值记录失败");
            return "toRechargeBack";
        }

        //封装请求快钱支付的参数
        Map<String, String> map = kuaiQianService.makeKuaiQianRequestParam(yuan, user.getName(), user.getPhone(),rechargeNo);
        model.addAllAttributes(map);

        return "kuaiQianForm";
    }
}
