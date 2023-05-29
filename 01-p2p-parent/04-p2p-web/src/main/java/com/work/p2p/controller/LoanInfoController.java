package com.work.p2p.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.work.cons.Constants;
import com.work.p2p.beans.loan.BidInfo;
import com.work.p2p.beans.loan.LoanInfo;
import com.work.p2p.beans.user.FinanceAccount;
import com.work.p2p.beans.user.User;
import com.work.p2p.beans.vo.BidUserVO;
import com.work.p2p.beans.vo.PaginationVO;
import com.work.p2p.services.loan.BidInfoService;
import com.work.p2p.services.loan.LoanInfoService;
import com.work.p2p.services.user.FinanceAccountService;
import com.work.utils.Result;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ClassName:LoanInfoController
 * Package:com.work.p2p.controller
 * Description: 描述信息
 *
 * @date:2023/4/18 10:34
 * @author:yueyue
 */
@Controller
@RequestMapping("/loan")
public class LoanInfoController {

    @Reference(interfaceClass = LoanInfoService.class,version = "1.0.0",check = false,timeout = 15000)
    LoanInfoService loanInfoService;

    @Reference(interfaceClass = BidInfoService.class,version = "1.0.0",check = false,timeout = 15000)
    BidInfoService bidInfoService;

    @Reference(interfaceClass = FinanceAccountService.class,version = "1.0.0",check = false,timeout = 15000)
    FinanceAccountService financeAccountService;

    /**投资页面分页呈现*/
    @RequestMapping("/loan")
    public String loan(HttpServletRequest request,
                       Model model,
                       @RequestParam (value = "ptype",required = false) Integer ptype,
                       @RequestParam (value = "currentPage", defaultValue = "1") Integer currentPage) {
        // 根据产品类型分页查询产品列表(产品类型、页码、每页显示的条数(S)) -> 返回(每页显示的数据，总条数)

        // 查询产品列表  ==> 返回值是 分页对象
        // 查询谁？ List<T>；分页信息(总条数)
        // 分页对象组成:1. 产品列表 List<T> 2. 分页信息(总条数)

        //1. 页面大小
        int pageSize = 9;
        //参数： 产品类型、当前页、每页查询的条数
        Map<String,Object> params = new HashMap<>();
        if(ObjectUtils.allNotNull(ptype)){ params.put("productType",ptype); }
        //2. 当前页
        params.put("currentPage",(currentPage-1)*pageSize);
        params.put("pageSize",pageSize);

        PaginationVO<LoanInfo> paginationVO =  loanInfoService.queryLoanInfoListByPage(params);

        //3. 计算总页数:总记录条数除以页面大小
        Integer totalPage =
                paginationVO.getTotalSize()%pageSize==0?paginationVO.getTotalSize()/pageSize :(paginationVO.getTotalSize()/pageSize)+1;

        //model.addAttribute("paginationVO",paginationVO);
        if(ObjectUtils.allNotNull(ptype)){
            model.addAttribute("ptype",ptype);//产品类型
        }
        model.addAttribute("loanInfoList",paginationVO.getDatas()); //产品列表
        model.addAttribute("currentPage",currentPage);//当前页码
        model.addAttribute("totalSize",paginationVO.getTotalSize());//总记录条数
        model.addAttribute("totalPage",totalPage);//总页数

        // 获取用户投资排行榜
        List<BidUserVO> bidUserLists = bidInfoService.queryBidUserTop();
        model.addAttribute("bidUserLists", bidUserLists);

        return "loan";
    }
    /**“产品详情页*/
    @RequestMapping("/loanInfo")
    public String loanInfo(HttpServletRequest request,
                           Model model,
                           @RequestParam("id") Integer id){

        // 页面构成
        // 1. 产品详情(根据ID查询产品信息)
        LoanInfo loanInfo = loanInfoService.queryLoanInfoById(id);
        model.addAttribute("loanInfo", loanInfo);

        // 2. 查询当前产品的投资记录
        Map<String, Object> params = new HashMap<>();
        params.put("loanId",id);
        params.put("currentPage",0);
        params.put("pageSize", 10);
        List<BidInfo> bidInfoList = bidInfoService.queryRecentlyBidInfoByLoanId(params);
        model.addAttribute("bidInfoList",bidInfoList);

        // 3. 如果是登录状态，则显示账户余额
        User user = (User) request.getSession().getAttribute(Constants.SESSION_USER);
        if (ObjectUtils.allNotNull(user)) {
            // 根据用户id查询账户信息
            FinanceAccount financeAccount = financeAccountService.queryFinanceAccountByUid(user.getId());
            model.addAttribute("availableMoney", financeAccount.getAvailableMoney());
        }
        return "/loanInfo";
    }



    /**立即投资*/
    @RequestMapping("/invest")
    @ResponseBody
    public Result invest(HttpServletRequest request,
                         @RequestParam("bidMoney") Double bidMoney,
                         @RequestParam("loanId") Integer loanId
                         ){
        try{
            User user = (User) request.getSession().getAttribute(Constants.SESSION_USER);
            Map<String,Object> params = new HashMap<>();
            params.put("bidMoney",bidMoney);
            params.put("loanId",loanId);
            params.put("userId",user.getId());
            params.put("phone",user.getPhone());
            //开始投资
            loanInfoService.invest(params);
        }catch (Exception e){
           // e.printStackTrace();
            return Result.fail("未知异常，投资失败");
        }
        return Result.success();
    }


}
