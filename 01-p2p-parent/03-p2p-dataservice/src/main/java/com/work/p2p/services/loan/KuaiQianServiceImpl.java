package com.work.p2p.services.loan;


import com.alibaba.dubbo.config.annotation.Service;
import com.work.cons.Constants;
import com.work.p2p.util.Pkipair;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * ClassName:KuaiQianServiceImpl
 * Package:com.work.p2p.services
 * Description: 封装请求快钱支付的参数的实现类
 *
 * @date:2023/4/29 21:08
 * @author:yueyue
 */
@Component
@Service(interfaceClass = KuaiQianService.class,version = "1.0.0",timeout = 15000)
public class KuaiQianServiceImpl implements KuaiQianService {

    @Autowired
    RedisTemplate<Object,Object> redisTemplate;

    @Override
    public Map<String, String> makeKuaiQianRequestParam(String yuan, String name, String phone, String rechargeNo) {

        Map<String,String> res = new HashMap<>();

        //人民币网关账号，该账号为11位人民币网关商户编号+01,该参数必填。
        String merchantAcctId = "1001214035601";//
        //编码方式，1代表 UTF-8; 2 代表 GBK; 3代表 GB2312 默认为1,该参数必填。
        String inputCharset = "1";
        //接收支付结果的页面地址，该参数一般置为空即可。
        String pageUrl = "";
        //服务器接收支付结果的后台地址，该参数务必填写，不能为空。
        //内网穿透
        //本地域名127.0.0.1:8084/kq/notify
        String bgUrl = "http://yueyue61.free.idcfengye.com";
        //网关版本，固定值：v2.0,该参数必填。
        String version =  "v2.0";
        //语言种类，1代表中文显示，2代表英文显示。默认为1,该参数必填。
        String language =  "1";
        //签名类型,该值为4，代表PKI加密方式,该参数必填。
        String signType =  "4";

        //支付人姓名,可以为空。
        String payerName= name;

        //支付人联系类型，1 代表电子邮件方式；2 代表手机联系方式。可以为空。
        String payerContactType =  "2";

        //支付人联系方式，与payerContactType设置对应，payerContactType为1，则填写邮箱地址；payerContactType为2，则填写手机号码。可以为空。
        String payerContact =  phone;

        //指定付款人，可以为空
        String payerIdType =  "3";
        //付款人标识，可以为空
        String payerId =  "KQ33151000";
        //付款人IP，可以为空
        String payerIP =  "192.168.1.1";

        //商户订单号，以下采用时间来定义订单号，商户可以根据自己订单号的定义规则来定义该值，不能为空。
        String orderId = rechargeNo;

        //订单金额，金额以“分”为单位，商户测试以1分测试即可，切勿以大金额测试。该参数必填。
        //String orderAmount = "1";
        String orderAmount = String.valueOf((int)(Double.parseDouble(yuan)*100));
        //订单提交时间，格式：yyyyMMddHHmmss，如：20071117020101，不能为空。
        String orderTime = new java.text.SimpleDateFormat("yyyyMMddHHmmss").format(new java.util.Date());
        //快钱时间戳，格式：yyyyMMddHHmmss，如：20071117020101， 可以为空
        String orderTimestamp= new java.text.SimpleDateFormat("yyyyMMddHHmmss").format(new java.util.Date());;
        //商品名称，可以为空。
        String productName= "Apple";
        //商品数量，可以为空。
        String productNum = "1";
        //商品代码，可以为空。
        String productId = "10000";
        //商品描述，可以为空。
        String productDesc = "Apple";
        //扩展字段1，商户可以传递自己需要的参数，支付完快钱会原值返回，可以为空。
        String ext1 = "扩展1";
        //扩展自段2，商户可以传递自己需要的参数，支付完快钱会原值返回，可以为空。
        String ext2 = "扩展2";
        //支付方式，一般为00，代表所有的支付方式。如果是银行直连商户，该值为10-1或10-2，必填。
        String payType = "00";
        //银行代码，如果payType为00，该值可以为空；如果payType为10-1或10-2，该值必须填写，具体请参考银行列表。
        String bankId = "";
        //同一订单禁止重复提交标志，实物购物车填1，虚拟产品用0。1代表只能提交一次，0代表在支付不成功情况下可以再提交。可为空。
        String redoFlag = "0";
        //快钱合作伙伴的帐户号，即商户编号，可为空。
        String pid = "";

        // signMsg 签名字符串 不可空，生成加密签名串
        String signMsgVal = "";
        signMsgVal = appendParam(signMsgVal, "inputCharset", inputCharset, res);
        signMsgVal = appendParam(signMsgVal, "pageUrl", pageUrl,res);
        signMsgVal = appendParam(signMsgVal, "bgUrl", bgUrl, res);
        signMsgVal = appendParam(signMsgVal, "version", version, res);
        signMsgVal = appendParam(signMsgVal, "language", language, res);
        signMsgVal = appendParam(signMsgVal, "signType", signType, res);
        signMsgVal = appendParam(signMsgVal, "merchantAcctId",merchantAcctId, res);
        signMsgVal = appendParam(signMsgVal, "payerName", payerName, res);
        signMsgVal = appendParam(signMsgVal, "payerContactType",payerContactType, res);
        signMsgVal = appendParam(signMsgVal, "payerContact", payerContact, res);
        signMsgVal = appendParam(signMsgVal, "payerIdType", payerIdType, res);
        signMsgVal = appendParam(signMsgVal, "payerId", payerId, res);
        signMsgVal = appendParam(signMsgVal, "payerIP", payerIP, res);
        signMsgVal = appendParam(signMsgVal, "orderId", orderId, res);
        signMsgVal = appendParam(signMsgVal, "orderAmount", orderAmount, res);
        signMsgVal = appendParam(signMsgVal, "orderTime", orderTime, res);
        signMsgVal = appendParam(signMsgVal, "orderTimestamp", orderTimestamp, res);
        signMsgVal = appendParam(signMsgVal, "productName", productName, res);
        signMsgVal = appendParam(signMsgVal, "productNum", productNum, res);
        signMsgVal = appendParam(signMsgVal, "productId", productId, res);
        signMsgVal = appendParam(signMsgVal, "productDesc", productDesc, res);
        signMsgVal = appendParam(signMsgVal, "ext1", ext1, res);
        signMsgVal = appendParam(signMsgVal, "ext2", ext2, res);
        signMsgVal = appendParam(signMsgVal, "payType", payType, res);
        signMsgVal = appendParam(signMsgVal, "bankId", bankId, res);
        signMsgVal = appendParam(signMsgVal, "redoFlag", redoFlag, res);
        signMsgVal = appendParam(signMsgVal, "pid", pid, res);



        System.out.println(signMsgVal);
        Pkipair pki = new Pkipair();
        String signMsg = pki.signMsg(signMsgVal);
        res.put("signMsg",signMsg);
        return res;
    }

    private String appendParam(String returns, String paramId, String paramValue,Map<String,String> res) {
        if (returns != "" && returns != null) {
            if (paramValue != "") { returns += "&" + paramId + "=" + paramValue; }
        }
        else {
            if (paramValue != "" && returns != null) { returns = paramId + "=" + paramValue; }
        }
        res.put(paramId,paramValue);
        return returns;
    }

//已经单独封装成工具类了，目的是充值记录里的订单编号和封装请求快钱支付的参数里的订单编号一致
// 生成订单号
//    private String generateOrderId() {
//        return "KQ" + DateFormatUtils.format(new Date(),"yyyyMMddHHmmssSSS") + redisTemplate.opsForValue().increment(Constants.RECHARGE_KUAIQIAN_SEQ);
//    }


}
