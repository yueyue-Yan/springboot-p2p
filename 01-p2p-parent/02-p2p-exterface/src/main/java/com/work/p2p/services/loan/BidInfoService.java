package com.work.p2p.services.loan;

import com.work.p2p.beans.loan.BidInfo;
import com.work.p2p.beans.vo.BidUserVO;

import java.util.List;
import java.util.Map;

public interface BidInfoService {
    //查询平台的累计成交金额
    Double qurryAllBidMoney();
    //查询产品的最近10条投资记录
    List<BidInfo> queryRecentlyBidInfoByLoanId(Map<String, Object> params);
    //获取投资排行榜列表
    List<BidUserVO> queryBidUserTop();
}
