package com.work.p2p.services.loan;

import com.alibaba.dubbo.config.annotation.Service;
import com.work.p2p.beans.loan.BidInfo;
import com.work.p2p.beans.loan.IncomeRecord;
import com.work.p2p.beans.loan.LoanInfo;
import com.work.p2p.mapper.loan.BidInfoMapper;
import com.work.p2p.mapper.loan.IncomeRecordMapper;
import com.work.p2p.mapper.loan.LoanInfoMapper;
import com.work.p2p.mapper.user.FinanceAccountMapper;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ClassName:IncomeRecordServiceImpl
 * Package:com.work.p2p.services.loan
 * Description: 收益计划Service的实现类
 *
 * @date:2023/4/28 20:11
 * @author:yueyue
 */
@Component
@Service(interfaceClass = IncomeRecordService.class,version = "1.0.0",timeout = 15000)
public class IncomeRecordServiceImpl implements IncomeRecordService{

    @Autowired
    LoanInfoMapper loanInfoMapper;

    @Autowired
    BidInfoMapper bidInfoMapper;

    @Autowired
    IncomeRecordMapper incomeRecordMapper;

    @Autowired
    FinanceAccountMapper financeAccountMapper;

    /**生成收益计划*/
    @Override
    @Transactional
    public void generateIncomePlan() throws Exception{
        //步骤：
        //1. 查询出产品的投资状态是否为已满标，返回一个list集合：b_loan_info表中的product_status字段是否为1，1表示已满标
        //2. 循环遍历处理已满标的产品
            //（1）获取并遍历满标产品们的投资记录b_bid_info
                //（2）生成用户的收益记录：根据产品表b_loan_info和投资记录表b_bid_info的信息在收益表b_income_record新增一条收益记录
            //（3）将该产品的产品状态由1该为2（0未满标，1已满标但未返还收益，2已满标且已返还收益）

        //1. 查询出产品的投资状态是否为已满标，返回一个list集合：b_loan_info表中的product_status字段是否为1，1表示已满标
        List<LoanInfo> loanInfoList = loanInfoMapper.selectLoanInfoListByProductStatus(1);
        //2. 循环遍历处理已满标的产品
        for(LoanInfo loanInfo:loanInfoList){
            //（1）获取并遍历满标产品们的投资记录b_income_record
            List<BidInfo> bidInfoList =  bidInfoMapper.selectBidInfoByLoanId(loanInfo.getId());
            for (BidInfo bidInfo:bidInfoList){
                IncomeRecord incomeRecord = new IncomeRecord();
                //收益记录中用户id、产品id，投资记录id和投资金额从投资记录表b_income_record中获取
                incomeRecord.setUid(bidInfo.getUid());
                incomeRecord.setLoanId(bidInfo.getLoanId());
                incomeRecord.setBidId(bidInfo.getId());
                incomeRecord.setBidMoney(bidInfo.getBidMoney());
                //（2）生成用户的收益记录：根据产品信息表b_loan_info和投资记录表b_bid_info的信息在收益表b_income_record新增一条收益记录
                //收益时间与收益金额
                Date incomeDate = null;
                Double incomeMoney = null;
                //①收益时间:从产品信息表获得b_loan_info：公式=产品满标时间(product_full_time)+投资周期(cycle)
                //②收益金额 公式：收益金额 = 投资金额*日利率*周期(注意投资金额在投资记录表b_bid_info中，利率和周期在产品信息表b_loan_info中)
                if(loanInfo.getProductType() == 0){
                    // 新手宝
                    //公式：
                    //收益时间 = 产品满标时间(product_full_time)+投资周期(cycle)天(Date+int用DateUtils)
                    //收益金额 = 投资金额 * 日利率 * 周期(天)
                    incomeDate = DateUtils.addDays(loanInfo.getProductFullTime(),loanInfo.getCycle());
                    incomeMoney = bidInfo.getBidMoney() * (loanInfo.getRate()/100/365) * loanInfo.getCycle();

                }
                else{
                    // 优选、散标
                    //公式：
                    //收益时间 = 产品满标时间(product_full_time)+投资周期(cycle)月(Date+int)
                    //收益金额 = 投资金额 * 日利率 * 周期(月)
                    incomeDate = DateUtils.addMonths(loanInfo.getProductFullTime(),loanInfo.getCycle());
                    incomeMoney = bidInfo.getBidMoney() * (loanInfo.getRate()/100/365) * loanInfo.getCycle() * 30;
                }
                //收益金额结果显示要求最长10位，保持两位小数
                incomeMoney = Math.round(incomeMoney*Math.pow(10,2))/Math.pow(10,2);
                incomeRecord.setIncomeStatus(0);//0未返还，1已返还
                incomeRecord.setIncomeDate(incomeDate);
                incomeRecord.setIncomeMoney(incomeMoney);

                int rows1 = incomeRecordMapper.insertSelective(incomeRecord);
                if(rows1 == 0){
                    throw new Exception("生成收益记录失败！");
                }
            }
            // (3) 将该产品的产品状态由1该为2（0未满标，1已满标但未返还收益，2已满标且已返还收益）
            loanInfo.setProductStatus(2);
            int rows2 = loanInfoMapper.updateByPrimaryKeySelective(loanInfo);
            if(rows2 == 0){
                throw new Exception("生成收益后产品状态由1改为2失败！");
            }
        }


    }
    /**返还收益*/
    @Override
    public void generateIncomeBack() throws Exception {
        //步骤
        //1. 查询出待返还收益的数据（当前时间=income_date且收益状态为0未返还）===返回结果为List<IncomeRecord>
        //2. 遍历待返还的收益记录
            //3. 根据收益记录中uid bidMoney incomeMoney 返回收益给相关用户的账户
            //4. 修改当前收益记录的状态，由0改为1（0未返还，1已返还）

        //1. 根据时间和状态查询出待返还收益的数据
        List<IncomeRecord> incomeRecordList = incomeRecordMapper.selectIncomeRecordListByCurDateAndIncomeStatus(0);
        //2. 遍历待返还的收益记录
        for (IncomeRecord incomeRecord : incomeRecordList) {
            //3. 根据uid bidMoney incomeMoney 返回收益给相关用户的账户
            Map<String,Object> params = new HashMap<>();
            params.put("uid",incomeRecord.getUid());
            params.put("bidMoney",incomeRecord.getBidMoney());
            params.put("incomeMoney",incomeRecord.getIncomeMoney());
            int rows1 =  financeAccountMapper.updateFinanceAccountByIncomeBack(params);
            if(rows1 == 0){
                throw new Exception("返还收益失败，更新账户表错误");
            }
            //4. 修改当前收益记录的状态，由0改为1（0未返还，1已返还）
            incomeRecord.setIncomeStatus(1);
            int rows2 = incomeRecordMapper.updateByPrimaryKeySelective(incomeRecord);
            if(rows2 == 0){
                throw new Exception("返还收益失败，更新收益状态错误");
            }
        }


    }
}
