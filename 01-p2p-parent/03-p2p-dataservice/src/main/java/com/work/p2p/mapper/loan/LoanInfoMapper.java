package com.work.p2p.mapper.loan;

import com.work.p2p.beans.loan.LoanInfo;

import java.util.List;
import java.util.Map;

public interface LoanInfoMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(LoanInfo record);

    int insertSelective(LoanInfo record);

    LoanInfo selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(LoanInfo record);

    int updateByPrimaryKey(LoanInfo record);

    /**
     * 获取平台历史年化收益率
     * @return
     */
    Double selectHistoryAvgRate();


    /**
     * 根据产品类型，返回产品列表
     * @param params
     * @return
     */
    List<LoanInfo> selectLoanInfoListByProductType(Map<String, Object> params);


    /**
     * 查询产品的总记录数
     * @param params
     * @return
     */
    Integer selectTotalSize(Map<String, Object> params);

    /**
     * 更新产品剩余可投金额
     * @param params
     * @return
     */
    int updateLeftProductMoney(Map<String, Object> params);

    /**
     * 根据产品状态（已满标），返回产品List集合
     * @param status
     * @return
     */
    List<LoanInfo> selectLoanInfoListByProductStatus(int status);

}