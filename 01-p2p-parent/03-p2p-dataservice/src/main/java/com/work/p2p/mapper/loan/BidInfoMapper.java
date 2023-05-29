package com.work.p2p.mapper.loan;

import com.work.p2p.beans.loan.BidInfo;

import java.util.List;
import java.util.Map;

public interface BidInfoMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(BidInfo record);

    int insertSelective(BidInfo record);

    BidInfo selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(BidInfo record);

    int updateByPrimaryKey(BidInfo record);
    //查询平台的累计成交金额
    Double selectAllBidMoney();
    //查询产品的最近10条投资记录
    List<BidInfo> selectRecentlyBidInfoByLoanId(Map<String, Object> params);
    //根据产品id返回该产品的所以投资记录（List）
    List<BidInfo> selectBidInfoByLoanId(Integer loanId);
}