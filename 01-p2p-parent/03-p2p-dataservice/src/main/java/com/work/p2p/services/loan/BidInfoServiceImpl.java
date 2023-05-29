package com.work.p2p.services.loan;

import com.alibaba.dubbo.config.annotation.Service;
import com.work.cons.Constants;
import com.work.p2p.beans.loan.BidInfo;
import com.work.p2p.beans.vo.BidUserVO;
import com.work.p2p.mapper.loan.BidInfoMapper;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Service(interfaceClass = BidInfoService.class,version = "1.0.0",timeout = 15000)
public class BidInfoServiceImpl implements BidInfoService {
    @Autowired
    BidInfoMapper bidInfoMapper;

    @Autowired
    RedisTemplate<Object,Object> redisTemplate;

    @Override
    public Double qurryAllBidMoney() {
        Double allBidMoney = (Double) redisTemplate.opsForValue().get(Constants.ALL_BID_MONEY);
        if(!ObjectUtils.allNotNull(allBidMoney)){
            synchronized (this){
                allBidMoney = (Double) redisTemplate.opsForValue().get(Constants.ALL_BID_MONEY);
                if(!ObjectUtils.allNotNull(allBidMoney)){
                    allBidMoney = bidInfoMapper.selectAllBidMoney();
                    redisTemplate.opsForValue().set(Constants.ALL_BID_MONEY,allBidMoney);
                }
            }
        }
        return allBidMoney;
    }

    @Override
    public List<BidInfo> queryRecentlyBidInfoByLoanId(Map<String, Object> params) {
        return bidInfoMapper.selectRecentlyBidInfoByLoanId(params);
    }
    //获取投资排行榜列表
    @Override
    public List<BidUserVO> queryBidUserTop() {
        //创建结果数组
        ArrayList<BidUserVO> bidUserVOS = new ArrayList<>();
        //遍历集合，取前五放入结果数组
        //reverseRangeWithScores(K key, long start, long end):索引倒序排列区间值
        Set<ZSetOperations.TypedTuple<Object>> typedTuples =
                redisTemplate.opsForZSet().reverseRangeWithScores(Constants.INVEST_TOP, 0, 5);
        Iterator<ZSetOperations.TypedTuple<Object>> iterator = typedTuples.iterator();
        BidUserVO bidUserVO;
        while (iterator.hasNext()){

            bidUserVO = new BidUserVO();
            ZSetOperations.TypedTuple<Object> next = iterator.next();

            //(key,value,score) --> (key,phone,bidmoney(总投资钱数))
            String phone = (String)next.getValue();
            Double score = next.getScore();

            bidUserVO.setPhone(phone);
            bidUserVO.setBidMoney(score);

            bidUserVOS.add(bidUserVO);
        }

        return bidUserVOS;
    }
}
