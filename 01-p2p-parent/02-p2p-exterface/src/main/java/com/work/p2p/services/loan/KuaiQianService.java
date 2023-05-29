package com.work.p2p.services.loan;

import java.util.Map;

/**
 * ClassName:KuaiQianService
 * Package:com.work.p2p.services
 * Description: 封装请求快钱支付的参数Service
 * @date:2023/4/29 21:05
 * @author:yueyue
 */
public interface KuaiQianService {

    Map<String, String> makeKuaiQianRequestParam(String yuan, String name, String phone, String rechargeNo);
}
