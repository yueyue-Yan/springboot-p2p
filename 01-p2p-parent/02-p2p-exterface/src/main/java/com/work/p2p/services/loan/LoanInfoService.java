package com.work.p2p.services.loan;

import com.work.p2p.beans.loan.LoanInfo;
import com.work.p2p.beans.vo.PaginationVO;

import java.util.List;
import java.util.Map;

public interface LoanInfoService {
    /**
     * 查询平台历史平均年化收益率
     */
    Double queryHistoryAvgRate();
    /**
     *根据产品类型查询产品列表
     *  在接口的方法上使用多行注释那么在方法的调用处，则可以使用快捷键ctrl+q快速查看方法的注释
     */
    List<LoanInfo> qurryLoanInfoListByProductType(Map<String, Object> params);

    /**
     * 分页查询产品列表
     */
    PaginationVO<LoanInfo> queryLoanInfoListByPage(Map<String,Object> params);

    /**
     * 根据id查询产品详情信息
     */
    LoanInfo queryLoanInfoById(Integer id);

    /**立即投资*/
    void invest(Map<String, Object> params) throws Exception;

}
