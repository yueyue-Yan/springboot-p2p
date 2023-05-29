package com.work.p2p.services.loan;

import com.alibaba.dubbo.config.annotation.Service;
import com.work.cons.Constants;
import com.work.p2p.beans.loan.BidInfo;
import com.work.p2p.beans.loan.LoanInfo;
import com.work.p2p.beans.vo.PaginationVO;
import com.work.p2p.mapper.loan.BidInfoMapper;
import com.work.p2p.mapper.loan.LoanInfoMapper;
import com.work.p2p.mapper.user.FinanceAccountMapper;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
@Service(interfaceClass = LoanInfoService.class,version = "1.0.0",timeout = 15000)
public class LoanInfoServiceImpl implements LoanInfoService {
    @Autowired
    private LoanInfoMapper loanInfoMapper;

    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    @Autowired
    private FinanceAccountMapper financeAccountMapper;

    @Autowired
    private BidInfoMapper bidInfoMapper;

    @Override
    public Double queryHistoryAvgRate() {
        //一个小优化：修改redis中key的序列化方式，为了可读性
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        //对于对实时查询不敏感的数据可以放进redis以减少mysql数据库的访问压力
        //1先访问redis
        Double historyAvgRate = (Double) redisTemplate.opsForValue().get(Constants.HISTORY_AVG_RATE);
        //双重验证+同步代码块
        if (!ObjectUtils.allNotNull(historyAvgRate)) {
            synchronized (this) {
                historyAvgRate = (Double) redisTemplate.opsForValue().get(Constants.HISTORY_AVG_RATE);
                if (!ObjectUtils.allNotNull(historyAvgRate)) {
                    //2如果redis中没有数据:查数据库，保存到redis一份以便下次不查数据库，设置有效时间7天
                    System.out.println("从MySQL数据库获取数据");
                    historyAvgRate = loanInfoMapper.selectHistoryAvgRate();
                    redisTemplate.opsForValue().set(Constants.HISTORY_AVG_RATE, historyAvgRate, 7, TimeUnit.DAYS);
                } else {
                    System.out.println("还是从redis中获取数据");
                }
            }
        } else {
            System.out.println("从redis中获取数据");
        }
        return historyAvgRate;
    }
    @Override
    public List<LoanInfo> qurryLoanInfoListByProductType(Map<String, Object> params) {
        return loanInfoMapper.selectLoanInfoListByProductType(params);
    }

    @Override
    public PaginationVO<LoanInfo> queryLoanInfoListByPage(Map<String, Object> params) {
        PaginationVO<LoanInfo> PaginationVO = new PaginationVO<>();
        //1.先查产品列表
        List<LoanInfo> loanInfos = loanInfoMapper.selectLoanInfoListByProductType(params);
        PaginationVO.setDatas(loanInfos);
        //2. 再查分页信息
        Integer totalSize =loanInfoMapper.selectTotalSize(params);
        PaginationVO.setTotalSize(totalSize);
        return PaginationVO;
    }

    @Override
    public LoanInfo queryLoanInfoById(Integer id) {
        return loanInfoMapper.selectByPrimaryKey(id);
    }
    /**立即投资实现*/
    @Transactional
    @Override
    public void invest(Map<String, Object> params) throws Exception {
        //投资总体步骤
        //1. 更新(减少)b_loan_info表的对应产品的剩余可投资金额left_product_money
        //2. 更新(减少)u_finance_account表的对应账户的余额available_money
        //3. 新增一条b_bid_info表的用户投资记录
        //4. 判断当前产品的剩余可投金额left_product_money是否为0，如果是，将该产品的产品状态由0改为1（b_laon_info表的product_status字段）
        //5.用户投资完成之后 将投资信息保存到Redis中

        //获取变量
        Integer loanId = (Integer) params.get("loanId");
        Integer userId = (Integer) params.get("userId");
        Double bidMoney = (Double) params.get("bidMoney");
        String phone = (String)params.get("phone");

        //1. 更新(减少)b_laon_info表的对应产品的剩余可投资金额left_product_money
        //乐观锁解决超卖问题步骤之一：获取version
        LoanInfo loanInfo = loanInfoMapper.selectByPrimaryKey(loanId);
        params.put("version",loanInfo.getVersion());
        //修改剩余可投资金额left_product_money
        int rows1 = loanInfoMapper.updateLeftProductMoney(params);
        if(rows1 == 0){
            throw new Exception("投资产品失败：01更新产品剩余可投资金额错误！");
        }
        //2. 更新(减少)u_finance_account表的对应账户的余额available_money
        int rows2 = financeAccountMapper.updateByBidMoney(params);
        if (rows2 == 0){
            throw new Exception("投资产品失败：02更新账户余额错误！");
        }
        //3. 新增一条b_bid_info表的用户投资记录
        BidInfo bidInfo = new BidInfo();
        bidInfo.setLoanId(loanId);
        bidInfo.setUid(userId);
        bidInfo.setBidMoney(bidMoney);
        bidInfo.setBidTime(new Date());
        bidInfo.setBidStatus(1);
        int rows3 = bidInfoMapper.insertSelective(bidInfo);
        if(rows3 == 0){
            throw new Exception("投资产品失败：03新增用户投资记录错误！");
        }
        //4. 判断当前产品的剩余可投金额left_product_money是否为0，如果是，将该产品的产品状态由0改为1（b_laon_info表的product_status字段）
        LoanInfo loanInfoDetail = loanInfoMapper.selectByPrimaryKey(loanId);
        if(loanInfoDetail.getLeftProductMoney() == 0){
            //产品的剩余可投金额为0，状态改为1表示已满标
            loanInfoDetail.setProductStatus(1);
            loanInfoDetail.setProductFullTime(new Date());
            int rows4 = loanInfoMapper.updateByPrimaryKeySelective(loanInfoDetail);
            if(rows4 == 0){
                throw new Exception("投资产品失败：04修改产品状态错误！");
            }
        }
        //5.用户投资完成之后 将投资信息保存到Redis中
        //incrementScore(K key, V value, double score): 增加变量中的元素的分值
        redisTemplate.opsForZSet().incrementScore(Constants.INVEST_TOP,phone,bidMoney);
    }
}




























